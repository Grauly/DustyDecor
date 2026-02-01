package grauly.dustydecor.block.vacpipe

import grauly.dustydecor.block.vacpipe.ConnectionState
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess

abstract class AbConnectableBlock(settings: Properties) : Block(settings), SimpleWaterloggedBlock {

    init {
        connections.forEach {
            registerDefaultState(defaultBlockState().setValue(it, ConnectionState.NONE))
        }
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false))
    }

    abstract fun isConnectable(
        state: BlockState,
        pos: BlockPos,
        world: LevelReader,
        connectionDirection: Direction
    ): Boolean

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        val workingState = getConnectionState(defaultBlockState(), ctx.clickedPos, ctx.level)
        return workingState
            .setValue(BlockStateProperties.WATERLOGGED, ctx.level.getFluidState(ctx.clickedPos).type == Fluids.WATER)
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
        if (state.getValueOrElse(BlockStateProperties.WATERLOGGED, false)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))
        }
        return getConnectionState(state, pos, world)
    }

    protected open fun getConnectionState(ownState: BlockState, ownPos: BlockPos, world: LevelReader): BlockState {
        val updatableConnections: List<EnumProperty<ConnectionState>> =
            connections.filter { needsUpdating(it, ownState, ownPos, world) }
        if (updatableConnections.isEmpty()) return ownState
        val takenDirections: MutableList<Direction> =
            connections.filter { !updatableConnections.contains(it) }
                .map { ownState.getValue(it).direction!! }.toMutableList()
        var workingState = ownState
        for (updateConnection: EnumProperty<ConnectionState> in updatableConnections) {
            val foundConnection = findConnection(ownPos, world, *takenDirections.toTypedArray())
            workingState = workingState.setValue(updateConnection, foundConnection)
            if (foundConnection != ConnectionState.NONE) {
                takenDirections.add(foundConnection.direction!!)
            }
        }
        return workingState
    }

    private fun needsUpdating(
        connection: EnumProperty<ConnectionState>,
        ownState: BlockState,
        ownPos: BlockPos,
        world: LevelReader
    ): Boolean {
        val currentConnection = ownState.getValue(connection)
        if (currentConnection == ConnectionState.NONE) return true
        val offsetPos = ownPos.relative(currentConnection.direction!!)
        return !canConnectTo(world.getBlockState(offsetPos), offsetPos, world, currentConnection.direction)
    }

    private fun canConnectTo(
        state: BlockState,
        pos: BlockPos,
        world: LevelReader,
        connectionDirection: Direction
    ): Boolean {
        if (!isConnectable(state, pos, world, connectionDirection)) return false
        if (state.block !is AbConnectableBlock) return true
        for (connection in connections) {
            val activeConnection = state.getValue(connection)
            if (activeConnection == ConnectionState.NONE) return true
            if (activeConnection.direction!!.opposite == connectionDirection) return true
        }
        return false
    }

    private fun needsConnecting(
        state: BlockState,
        pos: BlockPos,
        world: LevelReader,
        connectionDirection: Direction
    ): Boolean {
        return connections
            .filter { state.getValueOrElse(it, ConnectionState.NONE) != ConnectionState.NONE }
            .any { state.getValue(it).direction == connectionDirection.opposite }
    }

    protected fun findConnection(pos: BlockPos, world: LevelReader, vararg except: Direction): ConnectionState {
        var foundDirection: Direction? = null
        for (direction: Direction in Direction.entries) {
            if (except.contains(direction)) continue
            val offsetPos = pos.relative(direction)
            if (canConnectTo(world.getBlockState(offsetPos), pos, world, direction)) {
                if (needsConnecting(world.getBlockState(offsetPos), pos, world, direction)) {
                    return ConnectionState.Companion.fromDirection(direction)
                }
                if (foundDirection == null) {
                    foundDirection = direction
                }
            }
        }
        return ConnectionState.Companion.fromDirection(foundDirection)
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        if (mirror == Mirror.NONE) return state
        var returnState = state
        for (connection in connections) {
            if (state.getValueOrElse(connection, ConnectionState.NONE) != ConnectionState.NONE) {
                returnState =
                    returnState.setValue(
                        connection,
                        ConnectionState.Companion.fromDirection(mirror.mirror(state.getValue(connection).direction!!))
                    )
            }
        }
        return returnState
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        var returnState = state
        for (connection in connections) {
            if (state.getValueOrElse(connection, ConnectionState.NONE) != ConnectionState.NONE) {
                returnState = returnState.setValue(
                    connection,
                    ConnectionState.Companion.fromDirection(rotation.rotate(state.getValue(connection).direction!!))
                )
            }
        }
        return returnState
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(*connections.toTypedArray(), BlockStateProperties.WATERLOGGED)
    }

    override fun getFluidState(state: BlockState): FluidState =
        if (state.getValueOrElse(BlockStateProperties.WATERLOGGED, false)) Fluids.WATER.getSource(true) else super.getFluidState(state)

    companion object {
        val connections: List<EnumProperty<ConnectionState>> = listOf("a", "b", "c", "d", "e", "f")
            .subList(0, 2)
            .map { EnumProperty.create(it, ConnectionState::class.java) }
            .toList()

    }
}

