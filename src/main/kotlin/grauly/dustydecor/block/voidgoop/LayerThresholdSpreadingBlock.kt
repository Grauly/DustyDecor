package grauly.dustydecor.block.voidgoop

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.util.DebugUtils
import net.minecraft.block.*
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.item.context.DirectionalPlaceContext
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import kotlin.math.min

abstract class LayerThresholdSpreadingBlock(val threshold: Int, settings: Properties?) : FallingBlock(settings) {

    //TODO: add leaf piles
    //TODO: add sand/gravel piles

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(LAYERS, 1)
                .setValue(FALLING, false)
        )
    }

    override fun updateShape(
        state: BlockState,
        world: LevelReader,
        tickView: ScheduledTickAccess,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: RandomSource
    ): BlockState? {
        if (!world.isClientSide) {
            world as ServerLevel
            world.scheduleTick(pos, this, delayAfterPlace)
        }
        return state
    }

    override fun setPlacedBy(
        world: Level,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        super.setPlacedBy(world, pos, state, placer, itemStack)
        world.scheduleTick(pos, this, delayAfterPlace)
    }

    override fun tick(
        state: BlockState,
        world: ServerLevel,
        pos: BlockPos,
        random: RandomSource
    ) {
        val downState = world.getBlockState(pos.below())
        val canFallThrough = isFree(downState)
        val canMerge = if (downState.`is`(this)) {
            downState.getValue(LAYERS) < MAX_LAYERS
        } else false
        if (canMerge) {
            val ownLayers = state.getValue(LAYERS)
            val mergeLayers = min(MAX_LAYERS - downState.getValue(LAYERS), ownLayers)
            world.setBlockAndUpdate(pos.below(), downState.setValue(LAYERS, downState.getValue(LAYERS) + mergeLayers))
            val placeState = if (mergeLayers == ownLayers) Blocks.AIR.defaultBlockState() else state.setValue(LAYERS, ownLayers - mergeLayers)
            world.setBlockAndUpdate(pos, placeState)
            return
        }
        if (canFallThrough && pos.y >= world.minY) {
            val fallingBlock = FallingBlockEntity.fall(world, pos, state.setValue(FALLING, true))
            this.falling(fallingBlock)
        }
        val updatedState = world.getBlockState(pos)
        if (updatedState.isAir) return
        world.setBlockAndUpdate(pos, trySpread(pos, world, updatedState))
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        val placementBlockState = ctx.level.getBlockState(ctx.clickedPos)
        if (placementBlockState.`is`(this)) {
            val existingLayers = placementBlockState.getValue(LAYERS)
            return placementBlockState.setValue(LAYERS, min(MAX_LAYERS, existingLayers + 1))
        } else {
            return super.getStateForPlacement(ctx)
        }
    }

    override fun canSurvive(state: BlockState, world: LevelReader, pos: BlockPos): Boolean {
        val placedOnState = world.getBlockState(pos.below())
        return isFaceFull(placedOnState.getCollisionShape(world, pos.below()), Direction.UP) ||
                placedOnState.`is`(this) && placedOnState.getValue(LAYERS) == MAX_LAYERS
    }

    override fun canBeReplaced(state: BlockState, context: BlockPlaceContext): Boolean {
        if (context.itemInHand.`is`(this.asItem())) {
            return (state.getValue(LAYERS) < MAX_LAYERS)
        }
        return false
    }

    override fun falling(entity: FallingBlockEntity) {
        super.falling(entity)
        entity.dropItem = false
    }

    override fun onLand(
        world: Level,
        pos: BlockPos,
        fallingBlockState: BlockState,
        currentStateInPos: BlockState,
        fallingBlockEntity: FallingBlockEntity
    ) {
        if (!currentStateInPos.`is`(this)) {
            world.setBlockAndUpdate(pos, fallingBlockState.setValue(FALLING, false))
            return
        }
        val layersToIntegrate = fallingBlockState.getValue(LAYERS)
        val integrateBlockLayers = currentStateInPos.getValue(LAYERS)
        val totalLayers = layersToIntegrate + integrateBlockLayers
        if (totalLayers > MAX_LAYERS) {

            val overflowLayers = totalLayers - MAX_LAYERS
            val overflowPos = pos.above()
            val overflowState = world.getBlockState(overflowPos)
            val canPlace = overflowState.canSurvive(world, overflowPos)
            val canReplace = overflowState.canBeReplaced(
                DirectionalPlaceContext(
                    world, overflowPos, Direction.DOWN, this.asItem().defaultInstance, Direction.UP
                )
            )
            if (canPlace && canReplace) {
                world.setBlockAndUpdate(overflowPos, defaultBlockState().setValue(LAYERS, overflowLayers))
            }
        }
        world.setBlockAndUpdate(
            pos,
            currentStateInPos
                .setValue(LAYERS, min(MAX_LAYERS, totalLayers))
                .setValue(FALLING, false)
        )
    }

    private fun trySpread(pos: BlockPos, world: ServerLevel, state: BlockState): BlockState {
        val layers = state.getValue(LAYERS)
        val spreadTargets: Map<Direction, Int> = Direction.entries.filter { it.axis.isHorizontal }
            .associateWith { getSpreadDifferential(pos.relative(it), it, world) }
        if (spreadTargets.all { it.value >= layers }) return state //surrounded by higher/equal blocks
        if (spreadTargets.all { layers - it.value <= threshold }) return state //none have enough differential
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
            val offsetPos = pos.relative(entry.key)
            val offsetState = world.getBlockState(offsetPos)
            val workingState = if (offsetState.`is`(this)) {
                DustyDecorMod.logger.info("${DebugUtils.nameBlockPos(pos)} - ${entry.key} -> ${DebugUtils.nameBlockPos(offsetPos)}: $offsetState, ${entry.value}")
                offsetState.setValue(LAYERS, offsetState.getValue(LAYERS) + entry.value)
            } else {
                defaultBlockState().setValue(LAYERS, entry.value)
            }
            world.setBlockAndUpdate(offsetPos, workingState)
        }
        if (updatedLayerCount == 0) return Blocks.AIR.defaultBlockState()

        return state.setValue(LAYERS, updatedLayerCount)
    }

    private fun getSpreadDifferential(
        pos: BlockPos,
        searchDirection: Direction,
        world: ServerLevel,
        searchDepth: Int = 0,
        maxSearchDepth: Int = 1
    ): Int {
        val localState = world.getBlockState(pos)
        val canPlace = defaultBlockState().canSurvive(world, pos)
        val canReplace = localState.canBeReplaced(
            DirectionalPlaceContext(
                world, pos,
                searchDirection,
                this.asItem().defaultInstance,
                searchDirection.opposite
            )
        )
        if (!canReplace) return -searchDepth * MAX_LAYERS + MAX_LAYERS
        if (localState.`is`(this)) {
            DustyDecorMod.logger.info("${DebugUtils.nameBlockPos(pos)} (seek $searchDirection): $searchDepth, ${localState.getValue(LAYERS)}")
            return -searchDepth * MAX_LAYERS + localState.getValue(LAYERS)
        }
        if (!canPlace) {
            if (searchDepth >= maxSearchDepth) {
                return -searchDepth * MAX_LAYERS
            }
            DustyDecorMod.logger.info("${DebugUtils.nameBlockPos(pos)} (seek $searchDirection): $searchDepth, $localState, going down")
            return getSpreadDifferential(pos.below(), searchDirection, world, searchDepth + 1, maxSearchDepth)
        }
        return -searchDepth * MAX_LAYERS
    }

    override fun isPathfindable(state: BlockState, type: PathComputationType): Boolean {
        if (type != PathComputationType.LAND) return false
        return state.getValue(LAYERS) < 5
    }

    override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPES[state.getValue(LAYERS)]

    override fun getBlockSupportShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos
    ): VoxelShape = SHAPES[state.getValue(LAYERS)]

    override fun getCollisionShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPES[state.getValue(LAYERS)]

    override fun getVisualShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPES[state.getValue(LAYERS)]

    override fun getShadeBrightness(state: BlockState, world: BlockGetter, pos: BlockPos): Float {
        return 0.9f
    }

    override fun useShapeForLightOcclusion(state: BlockState): Boolean = true

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(LAYERS, FALLING)
    }

    companion object {
        val LAYERS: IntegerProperty = BlockStateProperties.LAYERS
        val FALLING: BooleanProperty = BooleanProperty.create("falling")
        const val MAX_LAYERS: Int = 8
        val SHAPES: Array<VoxelShape> = boxes(MAX_LAYERS) { layers ->
            column(16.0, 0.0, (layers * 2).toDouble())
        }
    }
}