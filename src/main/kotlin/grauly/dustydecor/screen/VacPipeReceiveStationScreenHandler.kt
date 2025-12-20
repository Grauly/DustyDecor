package grauly.dustydecor.screen

import grauly.dustydecor.ModScreenHandlerTypes
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos

class VacPipeReceiveStationScreenHandler : VacPipeStationScreenHandler<VacPipeReceiveStationScreenHandler> {
    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
        inventory: Inventory,
        context: ScreenHandlerContext,
        propertyDelegate: PropertyDelegate
    ) : super(
        ModScreenHandlerTypes.VAC_PIPE_STATION_RECEIVE_SCREEN_HANDLER,
        syncId,
        playerInventory,
        inventory,
        context,
        propertyDelegate
    )

    constructor(syncId: Int, playerInventory: PlayerInventory) : super(
        ModScreenHandlerTypes.VAC_PIPE_STATION_RECEIVE_SCREEN_HANDLER,
        syncId,
        playerInventory,
    )

    override fun addVariantSlots(inventory: Inventory) {
        addSlot(Slot(inventory, 0, 15, 73))
        addSlot(Slot(inventory, 1, 15, 43))
        addSlot(Slot(inventory, 2, 15, 18))
    }

    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {
        return super.onButtonClick(player, id)
    }
}