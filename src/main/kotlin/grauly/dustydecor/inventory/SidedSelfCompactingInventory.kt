package grauly.dustydecor.inventory

import net.minecraft.world.ContainerHelper
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.core.NonNullList
import net.minecraft.core.Direction

interface SidedSelfCompactingInventory : WorldlyContainer {
    val items: NonNullList<ItemStack>

    override fun clearContent() {
        setChanged()
        items.clear()
    }

    override fun getContainerSize(): Int = items.size
    override fun isEmpty(): Boolean = items.all { it.isEmpty }
    override fun getItem(slot: Int): ItemStack = items[slot]

    private fun compact() {
        val onlyItems = items.filter { it != ItemStack.EMPTY }
        clearContent()
        for (i in onlyItems.indices) {
            items[i] = onlyItems[i]
        }
        setChanged()
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val result = ContainerHelper.removeItem(items, slot, amount)
        if (!result.isEmpty) {
            compact()
            setChanged()
        }
        return result
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return removeItem(slot, getItem(slot).count)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
        if (stack.count > stack.maxStackSize) stack.count = stack.maxStackSize
        compact()
        setChanged()
    }

    fun insertDirections(): Set<Direction>
    fun extractDirections(): Set<Direction>

    override fun getSlotsForFace(side: Direction?): IntArray {
        if (side == null) return IntArray(0)
        val combinedSet: Set<Direction> = setOf(*insertDirections().toTypedArray(), *extractDirections().toTypedArray())
        val fullAccessArray = IntArray(items.size)
        for (i in fullAccessArray.indices) {
            fullAccessArray[i] = i
        }
        return if (combinedSet.contains(side)) fullAccessArray else IntArray(0)
    }

    override fun canPlaceItemThroughFace(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        if (dir == null) return false
        if (stack == null) return false
        if (!insertDirections().contains(dir)) return false
        for (item in items) {
            if (ItemStack.isSameItemSameComponents(item, stack) && item.count < item.maxStackSize || item.isEmpty) {
                return true
            }
        }
        return false
    }

    override fun canTakeItemThroughFace(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        if (dir == null) return false
        if (stack == null) return false
        if (!extractDirections().contains(dir)) return false
        for (item in items) {
            if (ItemStack.isSameItemSameComponents(item, stack)) return true
        }
        return true
    }
}