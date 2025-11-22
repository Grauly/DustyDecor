package grauly.dustydecor.screen

import grauly.dustydecor.ModScreenHandlerTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos

class VacPipeSendStationScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    inventory: Inventory,
    pos: BlockPos?
) : VacPipeStationScreenHandler<VacPipeSendStationScreenHandler>(
    ModScreenHandlerTypes.VAC_PIPE_STATION_SEND_SCREEN_HANDLER,
    syncId,
    playerInventory,
    inventory,
    pos
) {
    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId, playerInventory, SimpleInventory(3), null)

    override fun addVariantSlots(inventory: Inventory) {
        addSlot(Slot(inventory, 0, 15, 21))
        addSlot(Slot(inventory, 1, 15, 51))
        addSlot(Slot(inventory, 2, 15, 76))
    }

    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {
        return super.onButtonClick(player, id)
    }
}