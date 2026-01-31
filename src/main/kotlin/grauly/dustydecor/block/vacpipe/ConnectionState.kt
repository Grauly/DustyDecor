package grauly.dustydecor.block.vacpipe

import net.minecraft.util.StringRepresentable
import net.minecraft.core.Direction

enum class ConnectionState(
    val string: String,
    val direction: Direction?,
    val fallDown: Direction?
) : StringRepresentable {
    UP("up", Direction.UP, Direction.DOWN),
    DOWN("down", Direction.DOWN, Direction.DOWN),
    NORTH("north", Direction.NORTH, Direction.NORTH),
    SOUTH("south", Direction.SOUTH, Direction.NORTH),
    WEST("west", Direction.WEST, Direction.WEST),
    EAST("east", Direction.EAST, Direction.WEST),
    NONE("none", null, null);

    override fun getSerializedName(): String = string

    companion object {
        val CODEC: StringRepresentable.EnumCodec<ConnectionState> =
            StringRepresentable.fromEnum(ConnectionState.entries::toTypedArray)

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