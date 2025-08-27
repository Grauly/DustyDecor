package grauly.dustydecor.screen

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModScreenHandlerTypes
import grauly.dustydecor.inventory.SimpleCompactingInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot

class VacPipeStationScreenHandler(syncId: Int, playerInventory: PlayerInventory, private val inventory: Inventory) :
    ScreenHandler(ModScreenHandlerTypes.VAC_PIPE_STATION_SCREEN_HANDLER, syncId) {
    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId, playerInventory, SimpleCompactingInventory(3)) {
        DustyDecorMod.logger.info("sec con")
    }

    init {
        checkSize(inventory, 3)
        inventory.onOpen(playerInventory.player)
        for (i in 0..2) {
            addSlot(Slot(inventory, i, 8, 8 + i * 18))
        }

        for (y in 0..3) {
            for (x in 0..8) {
                addSlot(Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18))
            }
        }

        for (i in 0..8) {
            addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
        }
    }

    override fun quickMove(player: PlayerEntity, slotIndex: Int): ItemStack {
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

    override fun canUse(player: PlayerEntity): Boolean {
        return inventory.canPlayerUse(player)
    }
}