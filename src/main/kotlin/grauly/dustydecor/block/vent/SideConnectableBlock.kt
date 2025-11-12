package grauly.dustydecor.block.vent

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Waterloggable
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties.*
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

abstract class SideConnectableBlock(settings: Settings) : Block(settings.nonOpaque()), Waterloggable {

    init {
        defaultState = defaultState
            .with(UP, !FACE_CONNECTED)
            .with(DOWN, !FACE_CONNECTED)
            .with(NORTH, !FACE_CONNECTED)
            .with(SOUTH, !FACE_CONNECTED)
            .with(EAST, !FACE_CONNECTED)
            .with(WEST, !FACE_CONNECTED)
            .with(WATERLOGGED, false)
    }

    protected open fun getConnectionState(ownState: BlockState, ownPos: BlockPos, world: WorldView): BlockState {
        var returnState: BlockState = defaultState
        for (direction: Direction in Direction.entries) {
            if (canConnectTo(
                    world.getBlockState(ownPos.offset(direction)),
                    ownPos.offset(direction),
                    world,
                    direction
                )
            ) {
                returnState = returnState.with(getStateForDirection(direction), FACE_CONNECTED)
            }
        }
        return returnState
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val connectionState = getConnectionState(defaultState, ctx.blockPos, ctx.world)
        val fluidState = ctx.world.getFluidState(ctx.blockPos)
        return connectionState.with(WATERLOGGED, fluidState.fluid == Fluids.WATER)
    }

    abstract fun canConnectTo(state: BlockState, pos: BlockPos, world: WorldView, connectingSide: Direction): Boolean

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
        if (state.get(WATERLOGGED, false)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }
        return getConnectionState(state, pos, world).with(WATERLOGGED, state.get(WATERLOGGED, false))
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.get(WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else {
            super.getFluidState(state)
        }
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState =
        when (mirror) {
            BlockMirror.NONE -> state
            BlockMirror.FRONT_BACK -> state.with(
                EAST,
                state.get(WEST)
            ).with(
                WEST,
                state.get(EAST)
            )

            BlockMirror.LEFT_RIGHT -> state.with(
                NORTH,
                state.get(SOUTH)
            ).with(
                SOUTH,
                state.get(NORTH)
            )
        }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        var returnState: BlockState = state
        for (direction: Direction in Direction.entries) {
            if (state.get(getStateForDirection(direction), !FACE_CONNECTED) == FACE_CONNECTED) {
                returnState = returnState
                    .with(getStateForDirection(direction), !FACE_CONNECTED)
                    .with(getStateForDirection(rotation.rotate(direction)), FACE_CONNECTED)
            }
        }
        return returnState
    }

    fun getStateForDirection(direction: Direction): BooleanProperty = when (direction) {
        Direction.UP -> UP
        Direction.DOWN -> DOWN
        Direction.NORTH -> NORTH
        Direction.SOUTH -> SOUTH
        Direction.WEST -> WEST
        Direction.EAST -> EAST
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(UP, DOWN, NORTH, SOUTH, EAST, WEST, WATERLOGGED)
    }

    companion object {
        const val FACE_CONNECTED: Boolean = true
    }

}