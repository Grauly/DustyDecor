package grauly.dustydecor.block

import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.Direction

enum class ConnectionState(
    val string: String,
    val direction: Direction?,
    val fallDown: Direction?
) : StringIdentifiable {
    UP("up", Direction.UP, Direction.UP),
    DOWN("down", Direction.DOWN, Direction.UP),
    NORTH("north", Direction.NORTH, Direction.NORTH),
    SOUTH("south", Direction.SOUTH, Direction.NORTH),
    WEST("west", Direction.WEST, Direction.WEST),
    EAST("east", Direction.EAST, Direction.WEST),
    NONE("none", null, null);

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