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
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

abstract class AbConnectableBlock(settings: Settings) : Block(settings), Waterloggable {
    init {
        defaultState = defaultState
            .with(A, ConnectionState.NONE)
            .with(B, ConnectionState.NONE)
            .with(Properties.WATERLOGGED, false)
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
        val ownConnection = getFreeConnection(ownState) ?: return ownState
        val connection = findConnection(ownPos, world, ownState.get(getOtherState(ownConnection)).direction)
        if (connection == ConnectionState.NONE) return ownState.with(ownConnection, connection)
        return getConnectionState(ownState, ownPos, world).with(ownConnection, connection)
    }

    private fun canConnectTo(
        state: BlockState,
        pos: BlockPos,
        world: WorldView,
        connectionDirection: Direction
    ): Boolean {
        if (!isConnectable(state, pos, world, connectionDirection)) return false
        if (state.block !is AbConnectableBlock) return true
        return getFreeConnection(state) != null
    }

    private fun needsConnecting(
        state: BlockState,
        pos: BlockPos,
        world: WorldView,
        connectionDirection: Direction
    ): Boolean {
        if (state.get(A, ConnectionState.NONE).direction == connectionDirection.opposite) return true
        if (state.get(B, ConnectionState.NONE).direction == connectionDirection.opposite) return true
        return false
    }

    private fun connectPipes(
        start: BlockPos,
        other: BlockPos,
        startState: BlockState,
        otherState: BlockState,
        world: World,
        direction: Direction
    ) {
        world.setBlockState(
            start,
            startState.with(getFreeConnection(startState), ConnectionState.fromDirection(direction)),
            NOTIFY_LISTENERS
        )
        world.setBlockState(
            other,
            otherState.with(getFreeConnection(otherState), ConnectionState.fromDirection(direction.opposite)),
            NOTIFY_LISTENERS
        )
    }

    private fun getFreeConnection(state: BlockState): EnumProperty<ConnectionState>? {
        val a = state.get(A)
        val b = state.get(B)
        if (a == b) return B
        if (a == ConnectionState.NONE) return A
        if (b == ConnectionState.NONE) return B
        return null
    }

    private fun findConnection(pos: BlockPos, world: WorldView, except: Direction?): ConnectionState {
        var foundDirection: Direction? = null
        for (direction: Direction in Direction.entries) {
            if (except != null && direction == except) continue
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

    private fun getOtherState(property: EnumProperty<ConnectionState>): EnumProperty<ConnectionState> =
        if (property == A) B else A

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        if (mirror == BlockMirror.NONE) return state
        var returnState = state
        if (state.get(A, ConnectionState.NONE) != ConnectionState.NONE) {
            returnState =
                returnState.with(A, ConnectionState.fromDirection(mirror.apply(state.get(A).direction)))
        }
        if (state.get(B, ConnectionState.NONE) != ConnectionState.NONE) {
            returnState =
                returnState.with(A, ConnectionState.fromDirection(mirror.apply(state.get(B).direction)))
        }
        return returnState
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        var returnState = state
        if (state.get(A, ConnectionState.NONE) != ConnectionState.NONE) {
            returnState = returnState.with(A, ConnectionState.fromDirection(rotation.rotate(state.get(A).direction)))
        }
        if (state.get(B, ConnectionState.NONE) != ConnectionState.NONE) {
            returnState = returnState.with(A, ConnectionState.fromDirection(rotation.rotate(state.get(B).direction)))
        }
        return returnState
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(A, B, Properties.WATERLOGGED)
    }

    override fun getFluidState(state: BlockState): FluidState =
        if (state.get(Properties.WATERLOGGED, false)) Fluids.WATER.getStill(true) else super.getFluidState(state)

    companion object {
        val A: EnumProperty<ConnectionState> = EnumProperty.of("a", ConnectionState::class.java)
        val B: EnumProperty<ConnectionState> = EnumProperty.of("b", ConnectionState::class.java)
    }

    enum class ConnectionState(
        val string: String,
        val direction: Direction?
    ) : StringIdentifiable {
        UP("up", Direction.UP),
        DOWN("down", Direction.DOWN),
        NORTH("north", Direction.NORTH),
        SOUTH("south", Direction.SOUTH),
        WEST("west", Direction.WEST),
        EAST("east", Direction.EAST),
        NONE("none", null);

        override fun asString(): String = string

        companion object {
            val CODEC: StringIdentifiable.EnumCodec<ConnectionState> =
                StringIdentifiable.createCodec(ConnectionState.entries::toTypedArray)

            fun fromDirection(direction: Direction?) = when (direction) {
                Direction.UP -> UP
                Direction.DOWN -> DOWN
                Direction.NORTH -> NORTH
                Direction.SOUTH -> SOUTH
                Direction.WEST -> WEST
                Direction.EAST -> EAST
                else -> NONE
            }
        }
    }
}

