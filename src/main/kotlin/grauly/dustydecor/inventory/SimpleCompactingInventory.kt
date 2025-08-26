package grauly.dustydecor.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction

class SimpleCompactingInventory(slots: Int): SidedSelfCompactingInventory {
    override val items: DefaultedList<ItemStack> = DefaultedList.ofSize(slots)

    override fun insertDirections(): Set<Direction> {
        return Direction.entries.toSet()
    }

    override fun extractDirections(): Set<Direction> {
        return Direction.entries.toSet()
    }

    override fun markDirty() {
        //Not needed here... probably
    }

    override fun canPlayerUse(player: PlayerEntity?): Boolean = true
}