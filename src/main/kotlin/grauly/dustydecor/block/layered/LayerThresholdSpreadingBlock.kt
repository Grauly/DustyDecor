package grauly.dustydecor.block.layered

import grauly.dustydecor.DustyDecorMod
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.DirectionalPlaceContext
import net.minecraft.world.level.*
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FallingBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import kotlin.math.abs
import kotlin.math.min

abstract class LayerThresholdSpreadingBlock(val threshold: Int, settings: Properties) :
    FallingBlock(settings) {

    //TODO: add leaf piles
    //TODO: add sand/gravel piles

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(LAYERS, 1)
                .setValue(FALLING, false)
                .setValue(VELOCITY, Direction.DOWN)
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
    ): BlockState {
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
        itemStack: ItemStack
    ) {
        super.setPlacedBy(world, pos, state, placer, itemStack)
        world.scheduleTick(pos, this, delayAfterPlace)
    }

    override fun tick(
        state: BlockState,
        level: ServerLevel,
        pos: BlockPos,
        random: RandomSource
    ) {
        val downState = level.getBlockState(pos.below())
        val canFallThrough = isFree(downState)
        val canMerge = if (canJoinLayers(level, pos.below(), downState)) {
            downState.getValue(LAYERS) < MAX_LAYERS
        } else false
        if (canMerge) {
            val ownLayers = state.getValue(LAYERS)
            val mergeLayers = min(MAX_LAYERS - downState.getValue(LAYERS), ownLayers)
            level.setBlockAndUpdate(pos.below(), downState.setValue(LAYERS, downState.getValue(LAYERS) + mergeLayers))
            val placeState = if (mergeLayers == ownLayers) Blocks.AIR.defaultBlockState() else state.setValue(
                LAYERS,
                ownLayers - mergeLayers
            )
            level.setBlockAndUpdate(pos, placeState)
            return
        }
        if (canFallThrough && pos.y >= level.minY) {
            val fallingBlock = FallingBlockEntity.fall(level, pos, state.setValue(FALLING, true))
            this.falling(fallingBlock)
        }
        val updatedState = level.getBlockState(pos)
        if (updatedState.isAir) return
        level.setBlockAndUpdate(pos, trySpread(pos, level, updatedState))
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
        level: Level,
        pos: BlockPos,
        fallingBlockState: BlockState,
        currentStateInPos: BlockState,
        fallingBlockEntity: FallingBlockEntity
    ) {
        if (!canJoinLayers(level, pos, currentStateInPos)) {
            level.setBlockAndUpdate(pos, fallingBlockState.setValue(FALLING, false))
            return
        }
        val fallingLayers = fallingBlockState.getValue(LAYERS)
        val velocity = fallingBlockState.getValue(VELOCITY)
        val failedLayers = addLayers(level, pos, currentStateInPos.getValue(LAYERS))
        level.setBlockAndUpdate(pos, level.getBlockState(pos)
            .setValue(VELOCITY, velocity)
            .setValue(FALLING, false)
        )
        if (currentStateInPos.getValue(LAYERS) + fallingLayers > MAX_LAYERS && failedLayers == 0) {
            level.setBlockAndUpdate(pos.above(), level.getBlockState(pos.above()).setValue(VELOCITY, velocity))
        }
    }

    open fun onDestroyedByFall(
        level: Level,
        pos: BlockPos,
        fallingBlockState: BlockState,
    ) { }

    private fun trySpread(pos: BlockPos, level: ServerLevel, state: BlockState): BlockState {
        val layers = state.getValue(LAYERS)
        val velocity = state.getValue(VELOCITY)
        val spreadTargets: Map<Direction, Int> = Direction.entries.filter { it.axis.isHorizontal }
            .associateWith { getSpreadDifferential(pos.relative(it), it, level) }
        if (spreadTargets.all { it.value >= layers }) return state.setValue(VELOCITY, Direction.DOWN) //surrounded by higher/equal blocks
        if (spreadTargets.all { layers - it.value <= threshold }) return state.setValue(VELOCITY, Direction.DOWN) //none have enough differential
        var updatedLayerCount = layers
        val spreadActions: MutableMap<Direction, Int> = mutableMapOf()
        while (updatedLayerCount > 0) {
            val updatedSpreadTargets = spreadTargets.mapValues { it.value + (spreadActions[it.key] ?: 0) }
            if (updatedSpreadTargets.all { updatedLayerCount - it.value <= threshold }) { //none have enough differential left
                break
            }
            val lowestSpreadTargets = spreadTargets.filterValues { it <= updatedSpreadTargets.values.min() }
            val spreadDirection = if (lowestSpreadTargets.keys.contains(velocity)) velocity else lowestSpreadTargets.keys.random()
            spreadActions.compute(spreadDirection) { _, layers ->
                (layers ?: 0) + 1
            }
            updatedLayerCount -= 1
        }
        spreadActions.filterValues { it != 0 }.forEach { entry ->
            val offsetPos = pos.relative(entry.key)
            val offsetState = level.getBlockState(offsetPos)
            val workingState = if (canJoinLayers(level, offsetPos, offsetState)) {
/*
                DustyDecorMod.logger.info(
                    "${DebugUtils.nameBlockPos(pos)} - ${entry.key} -> ${
                        DebugUtils.nameBlockPos(
                            offsetPos
                        )
                    }: $offsetState, ${entry.value}"
                )
*/
                offsetState
                    .setValue(LAYERS, offsetState.getValue(LAYERS) + entry.value)
                    .setValue(VELOCITY, velocity)
            } else {
                defaultBlockState()
                    .setValue(LAYERS, entry.value)
                    .setValue(VELOCITY, velocity)
            }
            level.setBlockAndUpdate(offsetPos, workingState)
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
/*
            DustyDecorMod.logger.info(
                "${DebugUtils.nameBlockPos(pos)} (seek $searchDirection): $searchDepth, ${
                    localState.getValue(
                        LAYERS
                    )
                }"
            )
*/
            return -searchDepth * MAX_LAYERS + localState.getValue(LAYERS)
        }
        if (!canPlace) {
            if (searchDepth >= maxSearchDepth) {
                return -searchDepth * MAX_LAYERS
            }
            //DustyDecorMod.logger.info("${DebugUtils.nameBlockPos(pos)} (seek $searchDirection): $searchDepth, $localState, going down")
            return getSpreadDifferential(pos.below(), searchDirection, world, searchDepth + 1, maxSearchDepth)
        }
        return -searchDepth * MAX_LAYERS
    }

    /**
     * Places the given amount of layers
     *
     * @param level The Level this takes place in
     * @param pos The position
     * @param layers The amount of layers to place
     *
     * @return The amount of layers actually placed
     */
    fun placeLayers(level: Level, pos: BlockPos, layers: Int): Int {
        if (layers <= 0) return 0
        val existingState = level.getBlockState(pos)
        val maxPlaceableLayers = min(MAX_LAYERS, layers)
        if (canJoinLayers(level, pos, existingState)) {
            val existingLayers = existingState.getValue(LAYERS)
            val placedLayers = min(MAX_LAYERS - existingLayers, maxPlaceableLayers)
            val placementState = existingState.setValue(LAYERS, placedLayers)
            level.setBlockAndUpdate(pos, placementState)
            return placedLayers
        } else {
            level.setBlockAndUpdate(pos, defaultBlockState().setValue(LAYERS, maxPlaceableLayers))
            return maxPlaceableLayers
        }
    }

    /**
     * Attempts to deposit layers at the given position.
     * First searches down, then stacks up.
     * Stops upon hitting a ceiling.
     * By default, ignores placement conditions on the lowest point.
     * Does not respect spreading logic.
     *
     * @param level The Level this takes place in
     * @param pos The position to start at
     * @param layers The amount of layers to add
     * @param searchRange The maximum amount of blocks to search up/down
     * @param failOnPlacement If this should fail if the initial placement fails
     *
     * @return The amount of layers that where not placed
     */
    fun addLayers(level: Level, pos: BlockPos, layers: Int, searchRange: Int = 16, failOnPlacement: Boolean = false): Int {
        if (layers <= 0) return 0
        var workingPos = pos
        while (canReplaceTarget(level, workingPos, level.getBlockState(workingPos)) && abs(workingPos.y - pos.y) <= searchRange) {
            workingPos = workingPos.below()
        }
        workingPos = workingPos.above()
        if (!canBePut(level, workingPos, level.getBlockState(workingPos)) && failOnPlacement) {
            return layers
        }
        var remainingLayers = layers
        while (remainingLayers > 0 && abs(workingPos.y - pos.y) <= searchRange) {
            if (!canReplaceTarget(level, workingPos, level.getBlockState(workingPos))) return remainingLayers
            val layersToPlace = min(MAX_LAYERS, remainingLayers)
            val existingState = level.getBlockState(workingPos)
            if (canJoinLayers(level, workingPos, existingState)) {
                val existingLayers = existingState.getValue(LAYERS)
                val fillToLayers = min(MAX_LAYERS, existingLayers + layersToPlace)
                val failedToPlace = depositToLayer(level, workingPos, fillToLayers)
                remainingLayers -= (fillToLayers - failedToPlace)
            } else {
                val failedToPlace = depositToLayer(level, workingPos, layersToPlace)
                remainingLayers -= (layersToPlace - failedToPlace)
            }
            workingPos = workingPos.above()
        }
        return remainingLayers
    }

    /**
     * Ensures the given pos has at least the given amount of layers.
     * Builds a pillar up to the given pos.
     * Will ignore placement conditions on the lowest point.
     * Does not respect spreading logic.
     *
     * @param level The Level this takes place in
     * @param pos The position to start at
     * @param layers The amount of layers to enure
     *
     * @return The amount of layers added
     */
    fun ensureLayers(level: Level, pos: BlockPos, layers: Int): Int {
        val existingState = level.getBlockState(pos)
        if (canJoinLayers(level, pos, existingState)) {
            val existingLayers = existingState.getValue(LAYERS)
            if (existingLayers >= layers) return 0
            level.setBlockAndUpdate(pos, existingState.setValue(LAYERS, layers))
            return layers - existingLayers
        }
        level.setBlockAndUpdate(pos, defaultBlockState().setValue(LAYERS, layers))
        var addedLayers = layers
        var workingPos = pos.below()
        while (canReplaceTarget(level, workingPos, level.getBlockState(workingPos))) {
            val notDeposited = depositToLayer(level, workingPos, MAX_LAYERS)
            addedLayers += MAX_LAYERS - notDeposited
            workingPos = workingPos.below()
        }
        return addedLayers
    }

    /**
     * Attempts to deposit layers at the given position
     *
     * @param level The Level this takes place in
     * @param pos The position to change
     * @param targetLayers The resulting state will have this (or more) layers
     * It will have exactly the given amount of layers, if the existing state has <= the amount of layers
     * It will have more layers if the existing state has > layers.
     *
     * @return The amount of layers that where not deposited.
     * @throws IllegalArgumentException if targetLayers > MAX_LAYERS
     */
    fun depositToLayer(level: Level, pos: BlockPos, targetLayers: Int): Int {
        if (targetLayers > MAX_LAYERS) throw IllegalArgumentException("Attempting to deposit too many layers: $targetLayers > $MAX_LAYERS")
        val existingState = level.getBlockState(pos)
        if (!canReplaceTarget(level, pos, existingState)) return targetLayers
        if (canJoinLayers(level, pos, existingState) && existingState.hasProperty(LAYERS)) {
            val existingLayers = existingState.getValue(LAYERS)
            if (existingLayers >= targetLayers) return targetLayers
            level.setBlockAndUpdate(pos, existingState.setValue(LAYERS, targetLayers))
            return existingLayers
        }
        level.setBlockAndUpdate(pos, defaultBlockState().setValue(LAYERS, targetLayers))
        return 0
    }

    /**
     * @param level The Level this takes place in
     * @param pos The position to replace
     * @param existingState The existing state at that position
     *
     * Assumes the canBeReplaced allows self replacement
     */
    fun canReplaceTarget(level: Level, pos: BlockPos, existingState: BlockState): Boolean = existingState.canBeReplaced(
        DirectionalPlaceContext(
            level, pos,
            Direction.DOWN,
            this.asItem().defaultInstance,
            Direction.UP
        )
    )


    fun canBePut(level: Level, pos: BlockPos, state: BlockState): Boolean {
        val canPlace = defaultBlockState().canSurvive(level, pos)
        val canReplace = canReplaceTarget(level, pos, state)
        return canPlace && canReplace
    }

    open fun canJoinLayers(levelAccessor: LevelAccessor, pos: BlockPos, state: BlockState): Boolean {
        return state.`is`(this)
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
        builder.add(LAYERS, FALLING, VELOCITY)
    }

    companion object {
        val LAYERS: IntegerProperty = BlockStateProperties.LAYERS
        val FALLING: BooleanProperty = BooleanProperty.create("falling")
        val VELOCITY: EnumProperty<Direction> = EnumProperty.create("velocity", Direction::class.java, listOf(
            Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.DOWN
        ))
        const val MAX_LAYERS: Int = 8
        val SHAPES: Array<VoxelShape> = boxes(MAX_LAYERS) { layers ->
            column(16.0, 0.0, (layers * 2).toDouble())
        }
    }
}