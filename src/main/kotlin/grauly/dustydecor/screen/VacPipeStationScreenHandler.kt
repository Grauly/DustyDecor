package grauly.dustydecor.screen

import grauly.dustydecor.blockentity.vac_station.CopperGolemMode
import grauly.dustydecor.blockentity.vac_station.RedstoneEmissionMode
import grauly.dustydecor.blockentity.vac_station.SendMode
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.GOLEM_MODE
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.REDSTONE_MODE
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.SEND_MODE
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot

abstract class VacPipeStationScreenHandler<T : ScreenHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory,
    private val inventory: Inventory,
    private val context: ScreenHandlerContext,
    private val propertyDelegate: PropertyDelegate,
) : ScreenHandler(type, syncId) {

    init {
        checkSize(inventory, 3)
        inventory.onOpen(playerInventory.player)
        addVariantSlots(inventory)
        populateInventorySlots(playerInventory)
        addProperties(propertyDelegate)
    }

    abstract fun addVariantSlots(inventory: Inventory)

    protected fun populateInventorySlots(playerInventory: Inventory) {
        for (y in 0..2) {
            for (x in 0..8) {
                addSlot(Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 107 + y * 18))
            }
        }

        for (i in 0..8) {
            addSlot(Slot(playerInventory, i, 8 + i * 18, 165))
        }
    }

    override fun quickMove(
        player: PlayerEntity?,
        slotIndex: Int
    ): ItemStack? {
        val fromSlot: Slot = slots[slotIndex]
        if (!fromSlot.hasStack()) return ItemStack.EMPTY
        val movingStack = fromSlot.stack
        val originalStack = movingStack.copy()
        val fromInvToPlayer = slotIndex < inventory.size()
        val managedFit = insertItem(
            movingStack,
            if (fromInvToPlayer) inventory.size() else 0,
            if (fromInvToPlayer) slots.size else inventory.size(),
            fromInvToPlayer
        )
        if (!managedFit) return ItemStack.EMPTY
        if (movingStack.isEmpty) fromSlot.stack = ItemStack.EMPTY else fromSlot.markDirty()
        return originalStack
    }

    override fun canUse(player: PlayerEntity?): Boolean =
        inventory.canPlayerUse(player)

    fun getGolemMode(): CopperGolemMode {
        return CopperGolemMode.entries[propertyDelegate.get(GOLEM_MODE)]
    }
    fun getSendingMode(): SendMode {
        return SendMode.entries[propertyDelegate.get(SEND_MODE)]
    }

    fun getRedstoneMode(): RedstoneEmissionMode {
        return RedstoneEmissionMode.entries[propertyDelegate.get(REDSTONE_MODE)]
    }
}