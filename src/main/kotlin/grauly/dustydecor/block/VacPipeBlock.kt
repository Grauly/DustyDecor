package grauly.dustydecor.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.blockentity.VacPipeBlockEntity
import grauly.dustydecor.util.ToolUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

class VacPipeBlock(settings: Settings) : AbConnectableBlock(settings), BlockEntityProvider {

    init {
        windowStates.forEach {
            defaultState = defaultState.with(it, false)
        }
        defaultState = defaultState.with(SHOULD_HAVE_WINDOW, false)
        for (state: BlockState in stateManager.states) {
            val normalizedState = normalizeStateForShape(state)
            if (SHAPES.containsKey(normalizedState)) continue
            val shouldHaveWindow = normalizedState.get(SHOULD_HAVE_WINDOW)
            var workingShape = if (shouldHaveWindow) GLASS_CORE_SHAPE else CORE_SHAPE
            connections.forEach {
                val connectionDirection = normalizedState.get(it)
                if (connectionDirection != ConnectionState.NONE) {
                    workingShape = VoxelShapes.union(
                        workingShape,
                        if (shouldHaveWindow) GLASS_CONNECTOR_SHAPE_MAP[connectionDirection.direction]
                        else CONNECTOR_SHAPE_MAP[connectionDirection.direction]
                    )
                }
            }
            SHAPES[normalizedState] = workingShape
        }
    }

    fun alignPipeNetwork(
        state: BlockState,
        triggerState: BlockState,
        pos: BlockPos,
        triggerPos: BlockPos,
        triggerDirection: Direction,
        world: World
    ) {
        if (!state.isOf(ModBlocks.VAC_PIPE)) return
        if (pos.offset(triggerDirection.opposite) != triggerPos) return
        if (!triggerState.isOf(ModBlocks.VAC_PIPE_STATION)) {
            if (connections.map { triggerState.get(it).direction }.none { it == triggerDirection }) return
        }
        if (connections.map { state.get(it).direction }.none { it == triggerDirection.opposite }) return
        val triggerConnection = if (!triggerState.isOf(ModBlocks.VAC_PIPE_STATION)) {
            connections.first { triggerState.get(it).direction == triggerDirection }
        } else {
            if (triggerState.get(VacPipeStationBlock.SENDING)) connections[1] else connections[0]
        }
        val oppositeConnection = connections.first { it != triggerConnection }
        var newState = state
        if (state.get(oppositeConnection).direction != triggerDirection.opposite) {
            newState = flipConnections(state, pos, world)
        }
        val nextCheckDirection = newState.get(triggerConnection).direction ?: return
        val nextPos = pos.offset(nextCheckDirection)
        val nextState = world.getBlockState(nextPos)
        if (nextState.isOf(ModBlocks.VAC_PIPE_STATION)) {
            val sending = nextState.get(VacPipeStationBlock.SENDING)
            val seekDirectionIsA = triggerConnection == connections[0]
            if (sending && seekDirectionIsA) return
            if (!sending && !seekDirectionIsA) return
            world.setBlockState(pos, newState.with(triggerConnection, ConnectionState.NONE), NOTIFY_LISTENERS)
        }
        alignPipeNetwork(nextState, newState, nextPos, pos, nextCheckDirection, world)
    }

    private fun flipConnections(state: BlockState, pos: BlockPos, world: World): BlockState {
        val newState = state
            .with(connections[0], state.get(connections[1]))
            .with(connections[1], state.get(connections[0]))
            .with(windowStates[0], state.get(windowStates[1]))
            .with(windowStates[1], state.get(windowStates[0]))
        world.setBlockState(pos, newState, NOTIFY_LISTENERS)
        return newState
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext?
    ): VoxelShape {
        return SHAPES[normalizeStateForShape(state)]!!
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext?
    ): VoxelShape {
        return SHAPES[normalizeStateForShape(state)]!!
    }

    private fun normalizeStateForShape(state: BlockState): BlockState {
        var workingState = defaultState
        connections.forEach { workingState = workingState.with(it, state.get(it)) }
        workingState = workingState.with(SHOULD_HAVE_WINDOW, state.get(SHOULD_HAVE_WINDOW))
        return workingState
    }

    override fun isConnectable(
        state: BlockState,
        pos: BlockPos,
        world: WorldView,
        connectionDirection: Direction
    ): Boolean {
        if (state.isOf(ModBlocks.VAC_PIPE)) return true
        if (state.isOf(ModBlocks.VAC_PIPE_STATION)) {
            if (connectionDirection == Direction.DOWN) return true
        }
        return false
    }

    override fun onUseWithItem(
        stack: ItemStack,
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (ToolUtils.isScrewdriver(stack)) {
            val newWindowState: Boolean = togglePipeWindow(state, pos, world)
            ToolUtils.playScrewdriverSound(world, pos, player)
            world.playSound(
                player,
                pos,
                if (newWindowState) ModSoundEvents.BLOCK_VAP_PIPE_ADD_WINDOW else ModSoundEvents.BLOCK_VAP_PIPE_REMOVE_WINDOW,
                SoundCategory.BLOCKS
            )
        } else if (ToolUtils.isWrench(stack)) {
            ToolUtils.playWrenchSound(world, pos, player)
            val boxExpansion = 0.01
            val relativePos = hit.pos.subtract(Vec3d.of(pos))
            val clickedConnection: EnumProperty<ConnectionState>? = connections.firstOrNull {
                val connectionDirection = state.get(it, ConnectionState.NONE)
                if (connectionDirection == ConnectionState.NONE) {
                    return@firstOrNull false
                }
                val boundingBox = CONNECTOR_SHAPE_MAP[connectionDirection.direction]!!.boundingBox.expand(boxExpansion)
                return@firstOrNull boundingBox.contains(relativePos)
            }
            if (clickedConnection != null) {
                val success = tryDisableConnection(pos, state, clickedConnection, world)
                if (success) {
                    return ActionResult.SUCCESS
                    //TODO: success sound
                }
                //TODO: some fail sound
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit)
    }

    private fun tryDisableConnection(
        pos: BlockPos,
        state: BlockState,
        connection: EnumProperty<ConnectionState>,
        world: World,
        canTraverse: Boolean = true
    ): Boolean {
        val connectionDirection = state.get(connection, ConnectionState.NONE)
        if (connectionDirection == ConnectionState.NONE) return false
        val usedConnections =
            connections.map { state.get(it) }.filter { it != ConnectionState.NONE }.map { it.direction!! }
        val foundConnection =
            findConnection(pos, world, connectionDirection.direction!!, *usedConnections.toTypedArray())
        if (foundConnection == ConnectionState.NONE) {
            if (!canTraverse) return false
            val offsetPos = pos.offset(connectionDirection.direction)
            val offsetState = world.getBlockState(offsetPos)
            val otherConnection =
                connections.firstOrNull { offsetState.get(it) != ConnectionState.NONE && offsetState.get(it).direction!!.opposite == connectionDirection.direction }
            if (otherConnection == null) return false
            return tryDisableConnection(offsetPos, offsetState, otherConnection, world, false)
        }
        world.setBlockState(pos, updateWindows(state.with(connection, foundConnection), world, pos), NOTIFY_LISTENERS)
        return true
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
    ): BlockState {
        val superState = super.getStateForNeighborUpdate(
            state,
            world,
            tickView,
            pos,
            direction,
            neighborPos,
            neighborState,
            random
        )
        return updateWindows(superState, world, pos)
    }

    private fun updateWindows(
        state: BlockState,
        world: WorldView,
        pos: BlockPos
    ): BlockState {
        var workingState = state
        val shouldHaveWindow = state.get(SHOULD_HAVE_WINDOW, false)
        for (connection in connections) {
            if (shouldHaveWindow) {
                workingState = workingState.with(windowMap[connection], true)
                continue
            }
            val connectionDirection = state.get(connection, ConnectionState.NONE)
            if (connectionDirection == ConnectionState.NONE) {
                workingState = workingState.with(windowMap[connection], shouldHaveWindow)
                continue
            }
            val checkState = world.getBlockState(pos.offset(connectionDirection.direction))
            if (checkState.get(SHOULD_HAVE_WINDOW, false)) {
                workingState = workingState.with(windowMap[connection], true)
                continue
            }
            workingState = workingState.with(windowMap[connection], false)
        }
        return workingState
    }

    private fun togglePipeWindow(state: BlockState, pos: BlockPos, world: World): Boolean {
        val newState = !state.get(SHOULD_HAVE_WINDOW)
        world.setBlockState(pos, state.with(SHOULD_HAVE_WINDOW, newState), NOTIFY_LISTENERS)
        return newState
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(*windowStates.toTypedArray(), SHOULD_HAVE_WINDOW)
    }

    override fun canPathfindThrough(state: BlockState?, type: NavigationType?): Boolean = false

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return VacPipeBlockEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { world, pos, state, blockEntity ->
            if (blockEntity !is VacPipeBlockEntity) return@BlockEntityTicker
            blockEntity.tick(world, pos, state)
        }
    }

    companion object {
        val windowStates: List<BooleanProperty> = listOf("a_window", "b_window").map { BooleanProperty.of(it) }
        val windowMap: Map<EnumProperty<ConnectionState>, BooleanProperty> = mapOf(
            connections[0] to windowStates[0],
            connections[1] to windowStates[1]
        )
        val SHAPES: MutableMap<BlockState, VoxelShape> = mutableMapOf()
        val SHOULD_HAVE_WINDOW: BooleanProperty = BooleanProperty.of("should_have_window")
        val CONNECTOR_SHAPE_MAP: Map<Direction, VoxelShape> = VoxelShapes.createFacingShapeMap(
            VoxelShapes.cuboid(4.0 / 16, 4.0 / 16, 0.0, 12.0 / 16, 12.0 / 16, 4.0 / 16)
        )
        val GLASS_CONNECTOR_SHAPE_MAP: Map<Direction, VoxelShape> = VoxelShapes.createFacingShapeMap(
            VoxelShapes.cuboid(5.0 / 16, 5.0 / 16, 0.0, 11.0 / 16, 11.0 / 16, 5.0 / 16)
        )
        val CORE_SHAPE: VoxelShape = VoxelShapes.cuboid(4.0 / 16, 4.0 / 16, 4.0 / 16, 12.0 / 16, 12.0 / 16, 12.0 / 16)
        val GLASS_CORE_SHAPE: VoxelShape =
            VoxelShapes.cuboid(5.0 / 16, 5.0 / 16, 5.0 / 16, 11.0 / 16, 11.0 / 16, 11.0 / 16)
    }
}