package grauly.dustydecor.block.furniture

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids

open class HorizontalConnectingBlock(properties: Properties) : Block(properties) {
    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(NORTH, !FACE_CONNECTED)
                .setValue(EAST, !FACE_CONNECTED)
                .setValue(SOUTH, !FACE_CONNECTED)
                .setValue(WEST, !FACE_CONNECTED)
                .setValue(WATERLOGGED, false)
        )
    }

    override fun updateShape(
        state: BlockState,
        level: LevelReader,
        ticks: ScheduledTickAccess,
        pos: BlockPos,
        directionToNeighbour: Direction,
        neighbourPos: BlockPos,
        neighbourState: BlockState,
        random: RandomSource
    ): BlockState {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
        var workingState =
            super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
        workingState = getConnectionState(workingState, pos, level)
        return workingState
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val connectionState = getConnectionState(defaultBlockState(), context.clickedPos, context.level)
        val fluidState = context.level.getFluidState(context.clickedPos)
        return connectionState.setValue(WATERLOGGED, fluidState.type == Fluids.WATER)
    }

    protected fun getConnectionState(parentState: BlockState, pos: BlockPos, level: LevelReader): BlockState {
        var workingState = parentState
        CONNECTION_DIRECTIONS.forEach { direction ->
            val offsetPos = pos.relative(direction)
            val otherState = level.getBlockState(offsetPos)
            workingState = workingState.setValue(
                getPropertyForDirection(direction)!!, (otherState.`is`(this)) == FACE_CONNECTED
            )
        }
        return workingState
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(BlockStateProperties.WATERLOGGED)) {
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
        for (direction: Direction in CONNECTION_DIRECTIONS) {
            if (state.getValueOrElse(getPropertyForDirection(direction)!!, !FACE_CONNECTED) == FACE_CONNECTED) {
                returnState = returnState
                    .setValue(getPropertyForDirection(direction)!!, !FACE_CONNECTED)
                    .setValue(getPropertyForDirection(rotation.rotate(direction))!!, FACE_CONNECTED)
            }
        }
        return returnState
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(WATERLOGGED, NORTH, EAST, SOUTH, WEST)
    }

    companion object {
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
        val CONNECTION_DIRECTIONS = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
        val NORTH: BooleanProperty = BooleanProperty.create("north")
        val EAST: BooleanProperty = BooleanProperty.create("east")
        val SOUTH: BooleanProperty = BooleanProperty.create("south")
        val WEST: BooleanProperty = BooleanProperty.create("west")
        const val FACE_CONNECTED = true

        fun getPropertyForDirection(direction: Direction): Property<Boolean>? = when (direction) {
            Direction.NORTH -> NORTH
            Direction.EAST -> EAST
            Direction.SOUTH -> SOUTH
            Direction.WEST -> WEST
            else -> null
        }
    }
}