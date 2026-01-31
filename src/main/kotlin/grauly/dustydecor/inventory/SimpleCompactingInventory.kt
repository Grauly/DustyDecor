package grauly.dustydecor.inventory

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.core.NonNullList
import net.minecraft.core.Direction

class SimpleCompactingInventory(slots: Int): SidedSelfCompactingInventory {
    override val items: NonNullList<ItemStack> = NonNullList.withSize(slots, ItemStack.EMPTY)

    override fun insertDirections(): Set<Direction> {
        return Direction.entries.toSet()
    }

    override fun extractDirections(): Set<Direction> {
        return Direction.entries.toSet()
    }

    override fun setChanged() {
        //Not needed here... probably
    }

    override fun stillValid(player: Player?): Boolean = true
}