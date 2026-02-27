package grauly.dustydecor.block.furniture

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty

open class GranularHorizontalConnectingBlock(properties: Properties) : HorizontalConnectingBlock(properties) {
    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(NORTH_EAST, false)
                .setValue(NORTH_WEST, false)
                .setValue(SOUTH_EAST, false)
                .setValue(SOUTH_WEST, false)
        )
    }

    override fun getConnectionState(parentState: BlockState, pos: BlockPos, level: LevelReader): BlockState {
        var workingState = parentState
        for (offset in DIRECTION_PROPERTIES) {
            val offsetPos = pos.offset(offset.first)
            val offsetState = level.getBlockState(offsetPos)
            workingState = workingState.setValue(
                offset.second, (offsetState.`is`(this)) == FACE_CONNECTED
            )
        }
        return workingState
    }

    override fun mirror(
        state: BlockState,
        mirror: Mirror
    ): BlockState {
        return when (mirror) {
            Mirror.NONE -> {
                state
            }
            Mirror.LEFT_RIGHT -> {
                val north = state.getValue(NORTH)
                val north_east = state.getValue(NORTH_EAST)
                val north_west = state.getValue(NORTH_WEST)
                state
                    .setValue(NORTH, state.getValue(SOUTH))
                    .setValue(NORTH_EAST, state.getValue(SOUTH_EAST))
                    .setValue(NORTH_WEST, state.getValue(SOUTH_WEST))
                    .setValue(SOUTH, north)
                    .setValue(SOUTH_EAST, north_east)
                    .setValue(SOUTH_WEST, north_west)
            }
            Mirror.FRONT_BACK -> {
                val east = state.getValue(EAST)
                val north_east = state.getValue(NORTH_EAST)
                val south_east = state.getValue(SOUTH_EAST)
                state
                    .setValue(EAST, state.getValue(WEST))
                    .setValue(NORTH_EAST, state.getValue(NORTH_WEST))
                    .setValue(SOUTH_EAST, state.getValue(SOUTH_WEST))

                    .setValue(WEST, east)
                    .setValue(NORTH_WEST, north_east)
                    .setValue(SOUTH_WEST, south_east)
            }
        }
    }

    override fun rotate(
        state: BlockState,
        rotation: Rotation
    ): BlockState {
        val rotationOffset = when (rotation) {
            Rotation.NONE -> return state
            Rotation.CLOCKWISE_90 -> 2
            Rotation.CLOCKWISE_180 -> 4
            Rotation.COUNTERCLOCKWISE_90 -> 6
        }
        var workingState = state
        for (i in 0..7) {
            workingState = workingState.setValue(DIRECTION_PROPERTIES[(i + rotationOffset) % 8].second, state.getValue(DIRECTION_PROPERTIES[i].second))
        }
        return workingState
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST)
    }

    companion object {
        val NORTH_EAST: BooleanProperty = BooleanProperty.create("north_east")
        val NORTH_WEST: BooleanProperty = BooleanProperty.create("north_west")
        val SOUTH_EAST: BooleanProperty = BooleanProperty.create("south_east")
        val SOUTH_WEST: BooleanProperty = BooleanProperty.create("south_west")

        val DIRECTION_PROPERTIES: List<Pair<Vec3i, BooleanProperty>> = listOf(
            Direction.NORTH.unitVec3i to NORTH,
            Direction.NORTH.unitVec3i.offset(Direction.EAST.unitVec3i) to NORTH_EAST,
            Direction.EAST.unitVec3i to EAST,
            Direction.EAST.unitVec3i.offset(Direction.SOUTH.unitVec3i) to SOUTH_EAST,
            Direction.SOUTH.unitVec3i to SOUTH,
            Direction.SOUTH.unitVec3i.offset(Direction.WEST.unitVec3i) to SOUTH_WEST,
            Direction.WEST.unitVec3i to WEST,
            Direction.WEST.unitVec3i.offset(Direction.NORTH.unitVec3i) to NORTH_WEST,
        )
    }
}