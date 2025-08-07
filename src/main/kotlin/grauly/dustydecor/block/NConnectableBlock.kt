package grauly.dustydecor.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Waterloggable
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

abstract class NConnectableBlock(n: Int, settings: Settings) : Block(settings), Waterloggable {
    val connections: List<EnumProperty<ConnectionState>>

    init {
        if (n < 1) {
            throw IllegalArgumentException("Amount of connections must be at least 1")
        }
        if (n > 6) {
            //really, 6 would already be inefficient, since at that point, SideConnectableBlock would do the same ALOT simpler
            throw IllegalArgumentException("Amount of connections must be at most 6")
        }
        connections = mutableListOf("a", "b", "c", "d", "e", "f")
            .subList(0, n)
            .map { EnumProperty.of(it, ConnectionState::class.java) }
            .toList()
        connections.forEach {
            defaultState = defaultState.with(it, ConnectionState.NONE)
        }
        defaultState = defaultState.with(Properties.WATERLOGGED, false)
    }

    abstract fun isConnectable(
        state: BlockState,
        pos: BlockPos,
        world: WorldView,
        connectionDirection: Direction
    ): Boolean

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return getConnectionState(defaultState, ctx.blockPos, ctx.world)
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
        if (state.get(Properties.WATERLOGGED, false)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }
        return getConnectionState(state, pos, world)
    }

    protected open fun getConnectionState(ownState: BlockState, ownPos: BlockPos, world: WorldView): BlockState {
        val updatableConnections: List<EnumProperty<ConnectionState>> =
            connections.filter { !needsUpdating(it, ownState, ownPos, world) }
        if (updatableConnections.isEmpty()) return ownState
        val takenDirections: MutableList<Direction> =
            connections.toMutableList().filter { updatableConnections.contains(it) }
                .map { ownState.get(it).direction!! }.toMutableList()
        var workingState = ownState
        for (updateConnection: EnumProperty<ConnectionState> in updatableConnections) {
            val foundConnection = findConnection(ownPos, world, *takenDirections.toTypedArray())
            workingState = workingState.with(updateConnection, foundConnection)
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
        world: WorldView
    ): Boolean {
        val currentConnection = ownState.get(connection)
        if (currentConnection == ConnectionState.NONE) return false
        val offsetPos = ownPos.offset(currentConnection.direction)
        return canConnectTo(world.getBlockState(offsetPos), offsetPos, world, currentConnection.direction!!)
    }

    private fun canConnectTo(
        state: BlockState,
        pos: BlockPos,
        world: WorldView,
        connectionDirection: Direction
    ): Boolean {
        if (!isConnectable(state, pos, world, connectionDirection)) return false
        if (state.block !is NConnectableBlock) return true
        return getUnusedConnection(state) != null
    }

    private fun needsConnecting(
        state: BlockState,
        pos: BlockPos,
        world: WorldView,
        connectionDirection: Direction
    ): Boolean {
        return (state.block as NConnectableBlock).connections
            .filter { state.get(it) != ConnectionState.NONE }
            .any { state.get(it).direction == connectionDirection.opposite }
    }

    private fun getUnusedConnection(state: BlockState): EnumProperty<ConnectionState>? {
        return (state.block as NConnectableBlock).connections.firstOrNull { state.get(it) == ConnectionState.NONE }
    }

    private fun findConnection(pos: BlockPos, world: WorldView, vararg except: Direction): ConnectionState {
        var foundDirection: Direction? = null
        for (direction: Direction in Direction.entries) {
            if (except.contains(direction)) continue
            val offsetPos = pos.offset(direction)
            if (canConnectTo(world.getBlockState(offsetPos), pos, world, direction)) {
                if (needsConnecting(world.getBlockState(offsetPos), pos, world, direction)) {
                    return ConnectionState.fromDirection(direction)
                }
                if (foundDirection == null) {
                    foundDirection = direction
                }
            }
        }
        return ConnectionState.fromDirection(foundDirection)
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        if (mirror == BlockMirror.NONE) return state
        var returnState = state
        for (connection in connections) {
            if (state.get(connection, ConnectionState.NONE) != ConnectionState.NONE) {
                returnState =
                    returnState.with(
                        connection,
                        ConnectionState.fromDirection(mirror.apply(state.get(connection).direction))
                    )
            }
        }
        return returnState
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        var returnState = state
        for (connection in connections) {
            if (state.get(connection, ConnectionState.NONE) != ConnectionState.NONE) {
                returnState = returnState.with(
                    connection,
                    ConnectionState.fromDirection(rotation.rotate(state.get(connection).direction))
                )
            }
        }
        return returnState
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(*connections.toTypedArray(), Properties.WATERLOGGED)
    }

    override fun getFluidState(state: BlockState): FluidState =
        if (state.get(Properties.WATERLOGGED, false)) Fluids.WATER.getStill(true) else super.getFluidState(state)

}

