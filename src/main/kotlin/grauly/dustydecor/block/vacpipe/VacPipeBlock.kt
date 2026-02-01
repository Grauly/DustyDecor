package grauly.dustydecor.block.vacpipe

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.block.vacpipe.ConnectionState
import grauly.dustydecor.blockentity.VacPipeBlockEntity
import grauly.dustydecor.util.ToolUtils
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.level.block.SupportType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import java.sql.Connection
import kotlin.collections.get

class VacPipeBlock(settings: Properties) : AbConnectableBlock(settings), EntityBlock {

    //TODO: add splitter and merger blocks, THESE DO NOT SORT

    init {
        windowStates.forEach {
            registerDefaultState(defaultBlockState().setValue(it, false))
        }
        registerDefaultState(defaultBlockState().setValue(SHOULD_HAVE_WINDOW, false))
        for (state: BlockState in stateDefinition.possibleStates) {
            val normalizedState = normalizeStateForShape(state)
            if (SHAPES.containsKey(normalizedState)) continue
            val shouldHaveWindow = normalizedState.getValue(SHOULD_HAVE_WINDOW)
            var workingShape = if (shouldHaveWindow) GLASS_CORE_SHAPE else CORE_SHAPE
            connections.forEach {
                val connectionDirection = normalizedState.getValue(it)
                if (connectionDirection != ConnectionState.NONE) {
                    workingShape = Shapes.or(
                        workingShape,
                        (if (shouldHaveWindow) GLASS_CONNECTOR_SHAPE_MAP[connectionDirection.direction]
                        else CONNECTOR_SHAPE_MAP[connectionDirection.direction])!!
                    )
                }
            }
            SHAPES[normalizedState] = workingShape
        }
    }

    private fun getConnectionsFlippedState(state: BlockState) : BlockState {
        return state
            .setValue(connections[0], state.getValue(connections[1]))
            .setValue(connections[1], state.getValue(connections[0]))
            .setValue(windowStates[0], state.getValue(windowStates[1]))
            .setValue(windowStates[1], state.getValue(windowStates[0]))
    }

    private fun flipConnections(state: BlockState, pos: BlockPos, world: Level): BlockState {
        val newState = getConnectionsFlippedState(state)
        world.setBlock(pos, newState, UPDATE_CLIENTS)
        return newState
    }

    override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPES[normalizeStateForShape(state)]!!
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPES[normalizeStateForShape(state)]!!
    }

    private fun normalizeStateForShape(state: BlockState): BlockState {
        var workingState = defaultBlockState()
        connections.forEach { workingState = workingState.setValue(it, state.getValue(it)) }
        workingState = workingState.setValue(SHOULD_HAVE_WINDOW, state.getValue(SHOULD_HAVE_WINDOW))
        return workingState
    }

    override fun isConnectable(
        state: BlockState,
        pos: BlockPos,
        world: LevelReader,
        connectionDirection: Direction
    ): Boolean {
        if (state.`is`(ModBlocks.VAC_PIPE)) return true
        if (state.`is`(ModBlocks.VAC_PIPE_STATION)) {
            if (connectionDirection == Direction.DOWN) return true
        }
        return false
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        var workingState = state
        if (ToolUtils.isScrewdriver(stack)) {
            val newWindowState: Boolean = togglePipeWindow(state, pos, world)
            workingState = updateWindows(world.getBlockState(pos), world, pos)
            ToolUtils.playScrewdriverSound(world, pos, player)
            world.playSound(
                player,
                pos,
                if (newWindowState) ModSoundEvents.BLOCK_VAP_PIPE_ADD_WINDOW else ModSoundEvents.BLOCK_VAP_PIPE_REMOVE_WINDOW,
                SoundSource.BLOCKS
            )
        } else if (ToolUtils.isWrench(stack)) {
            ToolUtils.playWrenchSound(world, pos, player)
            val boxExpansion = 0.01
            val relativePos = hit.location.subtract(Vec3.atLowerCornerOf(pos))
            val clickedConnection: EnumProperty<ConnectionState>? = connections.firstOrNull {
                val connectionDirection = workingState.getValueOrElse(it, ConnectionState.NONE)
                if (connectionDirection == ConnectionState.NONE) {
                    return@firstOrNull false
                }
                val boundingBox = CONNECTOR_SHAPE_MAP[connectionDirection.direction]!!.bounds().inflate(boxExpansion)
                return@firstOrNull boundingBox.contains(relativePos)
            }
            if (clickedConnection != null) {
                val success = tryDisableConnection(pos, workingState, clickedConnection, world)
                if (success) {
                    return InteractionResult.SUCCESS
                    //TODO: success sound
                }
                //TODO: some fail sound
            } else {
                workingState = tryFixConnection(world, pos, workingState)
            }
        }
        if (workingState != state) {
            world.setBlock(pos, workingState, UPDATE_CLIENTS)
        }
        return super.useItemOn(stack, workingState, world, pos, player, hand, hit)
    }

    private fun tryDisableConnection(
        pos: BlockPos,
        state: BlockState,
        connection: EnumProperty<ConnectionState>,
        world: Level,
        canTraverse: Boolean = true
    ): Boolean {
        val connectionDirection = state.getValueOrElse(connection, ConnectionState.NONE)
        if (connectionDirection == ConnectionState.NONE) return false
        val usedConnections =
            connections.map { state.getValue(it) }.filter { it != ConnectionState.NONE }.map { it.direction!! }
        val foundConnection =
            findConnection(pos, world, connectionDirection.direction!!, *usedConnections.toTypedArray())
        if (foundConnection == ConnectionState.NONE) {
            if (!canTraverse) return false
            val offsetPos = pos.relative(connectionDirection.direction)
            val offsetState = world.getBlockState(offsetPos)
            val otherConnection =
                connections.firstOrNull { offsetState.getValueOrElse(it, ConnectionState.NONE) != ConnectionState.NONE && offsetState.getValue(it).direction!!.opposite == connectionDirection.direction }
            if (otherConnection == null) return false
            return tryDisableConnection(offsetPos, offsetState, otherConnection, world, false)
        }
        world.setBlock(pos, updateWindows(state.setValue(connection, foundConnection), world, pos), UPDATE_CLIENTS)
        return true
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
        var superState = super.updateShape(
            state,
            world,
            tickView,
            pos,
            direction,
            neighborPos,
            neighborState,
            random
        )
        superState = tryFixConnection(world, pos, superState)
        return updateWindows(superState, world, pos)
    }

    private fun tryFixConnection(world: LevelReader, pos: BlockPos, currentState: BlockState): BlockState {
        var workingState = currentState
        if (!isValidConnection(connections[0], world, pos, currentState)) {
            workingState = getConnectionsFlippedState(workingState)
        } else if(!isValidConnection(connections[1], world, pos, currentState)) {
            workingState = getConnectionsFlippedState(workingState)
        }
        return workingState
    }

    private fun isValidConnection(connection: EnumProperty<ConnectionState>, world: LevelReader, pos: BlockPos, state: BlockState): Boolean {
        val connectionState = state.getValue(connection)
        if (connectionState == ConnectionState.NONE) return true
        val connectionDirection = connectionState.direction!!
        val otherState = world.getBlockState(pos.relative(connectionDirection))
        if (otherState.`is`(ModBlocks.VAC_PIPE_STATION)) {
            val sending = otherState.getValue(VacPipeStationBlock.SENDING)
            if (connection == connections[0] && connectionDirection == Direction.DOWN && sending) return true
            if (connection == connections[1] && connectionDirection == Direction.DOWN && !sending) return true
            return false
        }
        if (otherState.`is`(ModBlocks.VAC_PIPE)) {
            val otherConnection = connections.first { it != connection}
            return otherState.getValue(otherConnection).direction?.opposite == connectionDirection
        }
        return false
    }

    override fun animateTick(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        random: RandomSource
    ) {
        super.animateTick(state, world, pos, random)
        displayLeaks(connections[0], false, state, world, pos)
        displayLeaks(connections[1], true, state, world, pos)
    }

    private fun displayLeaks(connection: EnumProperty<ConnectionState>, outflow: Boolean, state: BlockState, world: Level, pos: BlockPos) {
        if (!checkLeak(connection, state, world, pos)) return
        VacPipeStationBlock.indicatePipeLeak(world, pos, state.getValue(connection).direction!!, outflow)
    }

    private fun checkLeak(connection: EnumProperty<ConnectionState>, state: BlockState, world: Level, pos: BlockPos): Boolean {
        val connectionState = state.getValue(connection)
        if (connectionState == ConnectionState.NONE) return false
        val connectionDirection = connectionState.direction!!
        val otherPos = pos.relative(connectionDirection)
        val connectedToState = world.getBlockState(otherPos)
        if (isValidConnection(connection, world, pos, state)) return false
        val notCanFlow = connectedToState.isFaceSturdy(world, otherPos, connectionDirection.opposite, SupportType.CENTER)
        return !notCanFlow
    }

    private fun updateWindows(
        state: BlockState,
        world: LevelReader,
        pos: BlockPos
    ): BlockState {
        var workingState = state
        val shouldHaveWindow = state.getValueOrElse(SHOULD_HAVE_WINDOW, false)
        for (connection in connections) {
            if (shouldHaveWindow) {
                workingState = workingState.setValue(windowMap[connection]!!, true)
                continue
            }
            val connectionDirection = state.getValueOrElse(connection, ConnectionState.NONE)
            if (connectionDirection == ConnectionState.NONE) {
                workingState = workingState.setValue(windowMap[connection]!!, shouldHaveWindow)
                continue
            }
            val checkState = world.getBlockState(pos.relative(connectionDirection.direction!!))
            if (checkState.getValueOrElse(SHOULD_HAVE_WINDOW, false)) {
                workingState = workingState.setValue(windowMap[connection]!!, true)
                continue
            }
            workingState = workingState.setValue(windowMap[connection]!!, false)
        }
        return workingState
    }

    private fun togglePipeWindow(state: BlockState, pos: BlockPos, world: Level): Boolean {
        val newState = !state.getValue(SHOULD_HAVE_WINDOW)
        world.setBlock(pos, state.setValue(SHOULD_HAVE_WINDOW, newState), UPDATE_CLIENTS)
        return newState
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(*windowStates.toTypedArray(), SHOULD_HAVE_WINDOW)
    }

    override fun isPathfindable(state: BlockState, type: PathComputationType): Boolean = false

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return VacPipeBlockEntity(pos, state)
    }

    override fun <T : BlockEntity> getTicker(
        world: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return BlockEntityTicker { world, pos, state, blockEntity ->
            if (blockEntity !is VacPipeBlockEntity) return@BlockEntityTicker
            blockEntity.tick(world, pos, state)
        }
    }

    companion object {
        val windowStates: List<BooleanProperty> = listOf("a_window", "b_window").map { BooleanProperty.create(it) }
        val windowMap: Map<EnumProperty<ConnectionState>, BooleanProperty> = mapOf(
            connections[0] to windowStates[0],
            connections[1] to windowStates[1]
        )
        val SHAPES: MutableMap<BlockState, VoxelShape> = mutableMapOf()
        val SHOULD_HAVE_WINDOW: BooleanProperty = BooleanProperty.create("should_have_window")
        val CONNECTOR_SHAPE_MAP: Map<Direction, VoxelShape> = Shapes.rotateAll(
            Shapes.box(4.0 / 16, 4.0 / 16, 0.0, 12.0 / 16, 12.0 / 16, 4.0 / 16)
        )
        val GLASS_CONNECTOR_SHAPE_MAP: Map<Direction, VoxelShape> = Shapes.rotateAll(
            Shapes.box(5.0 / 16, 5.0 / 16, 0.0, 11.0 / 16, 11.0 / 16, 5.0 / 16)
        )
        val CORE_SHAPE: VoxelShape = Shapes.box(4.0 / 16, 4.0 / 16, 4.0 / 16, 12.0 / 16, 12.0 / 16, 12.0 / 16)
        val GLASS_CORE_SHAPE: VoxelShape =
            Shapes.box(5.0 / 16, 5.0 / 16, 5.0 / 16, 11.0 / 16, 11.0 / 16, 11.0 / 16)
    }
}