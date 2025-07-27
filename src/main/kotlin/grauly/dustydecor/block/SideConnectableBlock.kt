package grauly.dustydecor.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
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

abstract class SideConnectableBlock(settings: Settings) : Block(settings.nonOpaque()) {

    init {
        defaultState = defaultState
            .with(UP, FACE_CLOSED)
            .with(DOWN, FACE_CLOSED)
            .with(NORTH, FACE_CLOSED)
            .with(SOUTH, FACE_CLOSED)
            .with(EAST, FACE_CLOSED)
            .with(WEST, FACE_CLOSED)
    }

    private fun getConnectionState(pos: BlockPos, world: WorldView): BlockState {
        var returnState: BlockState = defaultState
        for (direction: Direction in Direction.entries) {
            if (canConnectTo(world.getBlockState(pos.offset(direction)))) {
                returnState = returnState.with(getStateForDirection(direction), !FACE_CLOSED)
            }
        }
        return returnState
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState = getConnectionState(ctx.blockPos, ctx.world)

    abstract fun canConnectTo(state: BlockState): Boolean

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: Random
    ): BlockState = getConnectionState(pos, world)

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState =
        when (mirror) {
            BlockMirror.NONE -> state.mirror(mirror)
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

    //TODO rotation

    private fun getStateForDirection(direction: Direction): BooleanProperty = when (direction) {
        Direction.UP -> UP
        Direction.DOWN -> DOWN
        Direction.NORTH -> NORTH
        Direction.SOUTH -> SOUTH
        Direction.WEST -> WEST
        Direction.EAST -> EAST
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(UP, DOWN, NORTH, SOUTH, EAST, WEST)
    }

    companion object {
        const val FACE_CLOSED: Boolean = true
    }

}