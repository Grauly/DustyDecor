package grauly.dustydecor.block

import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.Direction

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