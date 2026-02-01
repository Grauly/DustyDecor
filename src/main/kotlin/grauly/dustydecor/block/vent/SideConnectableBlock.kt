package grauly.dustydecor.block.vent

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties.*
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess

abstract class SideConnectableBlock(settings: Properties) : Block(settings.noOcclusion()), SimpleWaterloggedBlock {

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(UP, !FACE_CONNECTED)
                .setValue(DOWN, !FACE_CONNECTED)
                .setValue(NORTH, !FACE_CONNECTED)
                .setValue(SOUTH, !FACE_CONNECTED)
                .setValue(EAST, !FACE_CONNECTED)
                .setValue(WEST, !FACE_CONNECTED)
                .setValue(WATERLOGGED, false)
        )
    }

    protected open fun getConnectionState(ownState: BlockState, ownPos: BlockPos, world: LevelReader): BlockState {
        var returnState: BlockState = defaultBlockState()
        for (direction: Direction in Direction.entries) {
            if (canConnectTo(
                    world.getBlockState(ownPos.relative(direction)),
                    ownPos.relative(direction),
                    world,
                    direction
                )
            ) {
                returnState = returnState.setValue(getStateForDirection(direction), FACE_CONNECTED)
            }
        }
        return returnState
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val connectionState = getConnectionState(defaultBlockState(), ctx.clickedPos, ctx.level)
        val fluidState = ctx.level.getFluidState(ctx.clickedPos)
        return connectionState.setValue(WATERLOGGED, fluidState.type == Fluids.WATER)
    }

    abstract fun canConnectTo(state: BlockState, pos: BlockPos, world: LevelReader, connectingSide: Direction): Boolean

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
        if (state.getValueOrElse(WATERLOGGED, false)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))
        }
        return getConnectionState(state, pos, world).setValue(WATERLOGGED, state.getValueOrElse(WATERLOGGED, false))
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) {
            Fluids.WATER.getSource(false)
        } else {
            super.getFluidState(state)
        }
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState =
        when (mirror) {
            Mirror.NONE -> state
            Mirror.FRONT_BACK -> state.setValue(
                EAST,
                state.getValue(WEST)
            ).setValue(
                WEST,
                state.getValue(EAST)
            )

            Mirror.LEFT_RIGHT -> state.setValue(
                NORTH,
                state.getValue(SOUTH)
            ).setValue(
                SOUTH,
                state.getValue(NORTH)
            )
        }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        var returnState: BlockState = state
        for (direction: Direction in Direction.entries) {
            if (state.getValueOrElse(getStateForDirection(direction), !FACE_CONNECTED) == FACE_CONNECTED) {
                returnState = returnState
                    .setValue(getStateForDirection(direction), !FACE_CONNECTED)
                    .setValue(getStateForDirection(rotation.rotate(direction)), FACE_CONNECTED)
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

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST, WATERLOGGED)
    }

    companion object {
        const val FACE_CONNECTED: Boolean = true
    }

}