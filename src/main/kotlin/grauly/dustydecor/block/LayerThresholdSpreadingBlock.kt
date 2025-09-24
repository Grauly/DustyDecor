package grauly.dustydecor.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.util.DebugUtils
import net.minecraft.block.*
import net.minecraft.entity.FallingBlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.item.AutomaticItemPlacementContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView
import kotlin.math.min

abstract class LayerThresholdSpreadingBlock(val threshold: Int, settings: Settings?) : FallingBlock(settings) {

    init {
        defaultState = defaultState
            .with(LAYERS, 1)
            .with(FALLING, false)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: Random
    ): BlockState? {
        val postFallingShape = super.getStateForNeighborUpdate(
            state,
            world,
            tickView,
            pos,
            direction,
            neighborPos,
            neighborState,
            random
        )
        if (!world.isClient) {
            world as ServerWorld
            world.scheduleBlockTick(pos, this, fallDelay)
        }
        return postFallingShape
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        super.onPlaced(world, pos, state, placer, itemStack)
        world.scheduleBlockTick(pos, this, fallDelay)
    }

    override fun scheduledTick(
        state: BlockState,
        world: ServerWorld,
        pos: BlockPos,
        random: Random
    ) {
        val downState = world.getBlockState(pos.down())
        val canFallThrough = canFallThrough(downState)
        val canMerge = if (downState.isOf(this)) {
            downState.get(LAYERS) < MAX_LAYERS
        } else false
        if ((canFallThrough || canMerge) && pos.y >= world.bottomY) {
            val fallingBlock = FallingBlockEntity.spawnFromBlock(world, pos, state.with(FALLING, true))
            this.configureFallingBlockEntity(fallingBlock)
        }
        val updatedState = world.getBlockState(pos)
        if (updatedState.isAir) return
        world.setBlockState(pos, trySpread(pos, world, updatedState))
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val placementBlockState = ctx.world.getBlockState(ctx.blockPos)
        if (placementBlockState.isOf(this)) {
            val existingLayers = placementBlockState.get(LAYERS)
            return placementBlockState.with(LAYERS, min(MAX_LAYERS, existingLayers + 1))
        } else {
            return super.getPlacementState(ctx)
        }
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val placedOnState = world.getBlockState(pos.down())
        return isFaceFullSquare(placedOnState.getCollisionShape(world, pos.down()), Direction.UP) ||
                placedOnState.isOf(this) && placedOnState.get(LAYERS) == MAX_LAYERS
    }

    override fun canReplace(state: BlockState, context: ItemPlacementContext): Boolean {
        if (context.stack.isOf(this.asItem()) || context.stack.isEmpty) {
            return (state.get(LAYERS) < MAX_LAYERS)
        }
        return false
    }

    override fun configureFallingBlockEntity(entity: FallingBlockEntity) {
        super.configureFallingBlockEntity(entity)
        entity.dropItem = false
    }

    override fun onLanding(
        world: World,
        pos: BlockPos,
        fallingBlockState: BlockState,
        currentStateInPos: BlockState,
        fallingBlockEntity: FallingBlockEntity
    ) {
        if (!currentStateInPos.isOf(this)) {
            world.setBlockState(pos, fallingBlockState.with(FALLING, false))
            return
        }
        val layersToIntegrate = fallingBlockState.get(LAYERS)
        val integrateBlockLayers = currentStateInPos.get(LAYERS)
        val totalLayers = layersToIntegrate + integrateBlockLayers
        if (totalLayers > MAX_LAYERS) {

            val overflowLayers = totalLayers - MAX_LAYERS
            val overflowPos = pos.up()
            val overflowState = world.getBlockState(overflowPos)
            val canPlace = overflowState.canPlaceAt(world, overflowPos)
            val canReplace = overflowState.canReplace(
                AutomaticItemPlacementContext(
                    world, overflowPos, Direction.DOWN, this.asItem().defaultStack, Direction.UP
                )
            )
            if (canPlace && canReplace) {
                world.setBlockState(overflowPos, defaultState.with(LAYERS, overflowLayers))
            }
        }
        world.setBlockState(
            pos,
            currentStateInPos
                .with(LAYERS, min(MAX_LAYERS, totalLayers))
                .with(FALLING, false)
        )
    }

    private fun trySpread(pos: BlockPos, world: ServerWorld, state: BlockState): BlockState {
        val layers = state.get(LAYERS)
        val spreadTargets: Map<Direction, Int> = Direction.entries.filter { it.axis.isHorizontal }
            .associateWith { getSpreadDifferential(pos.offset(it), it, world) }
        DustyDecorMod.logger.info("${DebugUtils.nameBlockPos(pos)}: $spreadTargets")
        if (spreadTargets.all { it.value == MAX_LAYERS }) return state //surrounded by full blocks
        DustyDecorMod.logger.info("${DebugUtils.nameBlockPos(pos)}: get past max check")
        if (spreadTargets.all { it.value == layers }) return state //surrounded by same height
        DustyDecorMod.logger.info("${DebugUtils.nameBlockPos(pos)}: get past same check")
        if (spreadTargets.all { layers - it.value <= threshold }) return state //none have enough differential
        DustyDecorMod.logger.info("${DebugUtils.nameBlockPos(pos)}: get past differential check")
        var updatedLayerCount = layers
        val spreadActions: MutableMap<Direction, Int> = mutableMapOf()
        while (updatedLayerCount > 0) {
            val updatedSpreadTargets = spreadTargets.mapValues { it.value + (spreadActions[it.key] ?: 0) }
            if (updatedSpreadTargets.all { updatedLayerCount - it.value <= threshold }) { //none have enough differential left
                break
            }
            val lowestSpreadTargets = spreadTargets.filterValues { it <= updatedSpreadTargets.values.min() }
            spreadActions.compute(lowestSpreadTargets.keys.random()) { _, layers ->
                (layers ?: 0) + 1
            }
            updatedLayerCount -= 1
        }
        spreadActions.filterValues { it != 0 }.forEach { entry ->
            val offsetPos = pos.offset(entry.key)
            val offsetState = world.getBlockState(offsetPos)
            val workingState = if (offsetState.isOf(this)) {
                offsetState.with(LAYERS, offsetState.get(LAYERS) + entry.value)
            } else {
                defaultState.with(LAYERS, entry.value)
            }
            world.setBlockState(offsetPos, workingState)
        }
        if (updatedLayerCount == 0) return Blocks.AIR.defaultState
        return state.with(LAYERS, updatedLayerCount)
    }

    private fun getSpreadDifferential(
        pos: BlockPos,
        searchDirection: Direction,
        world: ServerWorld,
        searchDepth: Int = 0,
        maxSearchDepth: Int = 1
    ): Int {
        val localState = world.getBlockState(pos)
        val canPlace = defaultState.canPlaceAt(world, pos)
        val canReplace = localState.canReplace(
            AutomaticItemPlacementContext(
                world, pos,
                searchDirection,
                this.asItem().defaultStack,
                searchDirection.opposite
            )
        )
        if (!canReplace) return -searchDepth * MAX_LAYERS + MAX_LAYERS
        if (!canPlace) {
            if (searchDepth >= maxSearchDepth) {
                return -searchDepth * MAX_LAYERS
            }
            return getSpreadDifferential(pos.down(), searchDirection, world, searchDepth + 1, maxSearchDepth)
        }
        if (localState.isOf(this)) {
            return -searchDepth * MAX_LAYERS + localState.get(LAYERS)
        }
        return -searchDepth * MAX_LAYERS
    }

    override fun canPathfindThrough(state: BlockState, type: NavigationType): Boolean {
        if (type != NavigationType.LAND) return false
        return state.get(LAYERS) < 5
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape = SHAPES[state.get(LAYERS)]

    override fun getSidesShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos
    ): VoxelShape = SHAPES[state.get(LAYERS)]

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape = SHAPES[state.get(LAYERS)]

    override fun getCameraCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape = SHAPES[state.get(LAYERS)]

    override fun getAmbientOcclusionLightLevel(state: BlockState, world: BlockView, pos: BlockPos): Float {
        return if (state.get(LAYERS) == MAX_LAYERS) 0.2f else 1f
    }

    override fun hasSidedTransparency(state: BlockState): Boolean = true

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(LAYERS, FALLING)
    }

    companion object {
        val LAYERS: IntProperty = Properties.LAYERS
        val FALLING: BooleanProperty = BooleanProperty.of("falling")
        const val MAX_LAYERS: Int = 8
        val SHAPES: Array<VoxelShape> = createShapeArray(MAX_LAYERS) { layers ->
            createColumnShape(16.0, 0.0, (layers * 2).toDouble())
        }
        val SPREAD_TARGETS = listOf(
            Vec3i(1, 0, 0),
            Vec3i(-1, 0, 0),
            Vec3i(0, 0, 1),
            Vec3i(0, 0, -1),
            Vec3i(1, -1, 0),
            Vec3i(-1, -1, 0),
            Vec3i(0, -1, 1),
            Vec3i(0, -1, -1),
        )
    }
}