package grauly.dustydecor.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties.*
import net.minecraft.util.BlockMirror
import net.minecraft.util.math.Direction

open class SideConnectableBlock(settings: Settings) : Block(settings.nonOpaque()) {

    init {
        defaultState = defaultState
            .with(UP, false)
            .with(DOWN, false)
            .with(NORTH, false)
            .with(SOUTH, false)
            .with(EAST, false)
            .with(WEST, false)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val placeDirection: Direction = ctx.side.opposite
        val placeAgainstPos = ctx.blockPos.offset(placeDirection)
        val placedAgainstBlock: BlockState = ctx.world.getBlockState(placeAgainstPos)
        //TODO("seperate connection code")
        if (!placedAgainstBlock.isOf(this)) return defaultState
        val returnState = defaultState.with(getStateForDirection(placeDirection), true)
        if (ctx.shouldCancelInteraction()) return returnState
        ctx.world.setBlockState(
            placeAgainstPos,
            placedAgainstBlock.withIfExists(getStateForDirection(placeDirection.opposite), true)
        )
        return returnState
    }

    open fun canConnect(connectionState: BlockState, direction: Direction): Boolean {
        return isViableConnectionTarget(connectionState)
    }

    open fun isViableConnectionTarget(targetState: BlockState): Boolean {
        return targetState.block is SideConnectableBlock
    }

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

}