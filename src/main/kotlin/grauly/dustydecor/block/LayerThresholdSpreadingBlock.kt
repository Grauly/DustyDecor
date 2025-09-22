package grauly.dustydecor.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FallingBlock
import net.minecraft.block.ShapeContext
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
        super.scheduledTick(state.with(FALLING, true), world, pos, random)
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
        val spreadTargets: Map<Direction, Int> = Direction.entries.filter { it.axis.isHorizontal }.map {
            val offset = pos.offset(it)
            val offsetState = world.getBlockState(offset)
            val canPlace = offsetState.canPlaceAt(world, offset)
            val canReplace = offsetState.canReplace(
                AutomaticItemPlacementContext(
                    world,
                    offset,
                    it,
                    this.asItem().defaultStack,
                    it.opposite
                )
            )
            if (canPlace && canReplace) {
                if (offsetState.isOf(this)) {
                    return@map it to offsetState.get(LAYERS)
                }
                return@map it to 0
            }
            return@map null
        }.filterNotNull().toMap()
        if (spreadTargets.isEmpty()) return state
        var updatedLayerCount = layers
        val spreadActions: MutableMap<Direction, Int> = mutableMapOf()
        for (i in 1..layers) {
            val updatedSpreadTargets: Map<Direction, Int> =
                spreadTargets.map { entry -> entry.key to entry.value + (spreadActions[entry.key] ?: 0) }.toMap()
            val lowest = updatedSpreadTargets.values.min()
            val lowestSpreadTargets = spreadTargets.filterValues { it <= lowest }
            if (lowest < updatedLayerCount - threshold) {
                spreadActions.compute(lowestSpreadTargets.keys.random()) { _, l ->
                    if (l != null) l + 1 else 1
                }
                updatedLayerCount -= 1
            } else {
                break
            }
        }
        spreadActions.forEach { entry ->
            val offsetPos = pos.offset(entry.key)
            val offsetState = world.getBlockState(offsetPos)
            if (offsetState.isOf(this)) {
                world.setBlockState(offsetPos, offsetState.with(LAYERS, offsetState.get(LAYERS) + entry.value))
            } else {
                world.setBlockState(offsetPos, defaultState.with(LAYERS, entry.value))
            }
        }
        return state.with(LAYERS, updatedLayerCount)
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
        return if (state.get(LAYERS) == MAX_LAYERS) 1f else 0.2f
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
    }
}