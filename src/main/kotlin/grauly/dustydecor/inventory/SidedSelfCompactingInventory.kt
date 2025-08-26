package grauly.dustydecor.inventory

import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction

interface SidedSelfCompactingInventory : SidedInventory {
    val items: DefaultedList<ItemStack>

    override fun clear() {
        markDirty()
        items.clear()
    }

    override fun size(): Int = items.size
    override fun isEmpty(): Boolean = items.all { it.isEmpty }
    override fun getStack(slot: Int): ItemStack = items[slot]

    private fun compact() {
        val onlyItems = items.filter { it != ItemStack.EMPTY }
        clear()
        for (i in onlyItems.indices) {
            items[i] = onlyItems[i]
        }
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        val result = Inventories.splitStack(items, slot, amount)
        if (!result.isEmpty) {
            compact()
            markDirty()
        }
        return result
    }

    override fun removeStack(slot: Int): ItemStack {
        return removeStack(slot, getStack(slot).count)
    }

    override fun setStack(slot: Int, stack: ItemStack) {
        items[slot] = stack
        if (stack.count > stack.maxCount) stack.count = stack.maxCount
        compact()
        markDirty()
    }

    fun insertDirections(): Set<Direction>
    fun extractDirections(): Set<Direction>

    override fun getAvailableSlots(side: Direction?): IntArray {
        if (side == null) return IntArray(0)
        val combinedSet: Set<Direction> = setOf(*insertDirections().toTypedArray(), *extractDirections().toTypedArray())
        val fullAccessArray = IntArray(items.size)
        for (i in fullAccessArray.indices) {
            fullAccessArray[i] = i
        }
        return if (combinedSet.contains(side)) fullAccessArray else IntArray(0)
    }

    override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        if (dir == null) return false
        if (stack == null) return false
        if (!insertDirections().contains(dir)) return false
        for (item in items) {
            if (ItemStack.areItemsAndComponentsEqual(
                    item,
                    stack
                ) && item.count < item.maxCount || item == ItemStack.EMPTY
            ) return true
        }
        return false
    }

    override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        if (dir == null) return false
        if (stack == null) return false
        if (!extractDirections().contains(dir)) return false
        for (item in items) {
            if (ItemStack.areItemsAndComponentsEqual(item, stack)) return true
        }
        return false
    }
}