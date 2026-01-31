package grauly.dustydecor.screen

import grauly.dustydecor.ModScreenHandlerTypes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot

class VacPipeSendStationScreenHandler : VacPipeStationScreenHandler<VacPipeSendStationScreenHandler> {
    constructor(
        syncId: Int,
        playerInventory: Inventory,
        inventory: Container,
        context: ContainerLevelAccess,
        propertyDelegate: ContainerData
    ) : super(
        ModScreenHandlerTypes.VAC_PIPE_STATION_SEND_SCREEN_HANDLER,
        syncId,
        playerInventory,
        inventory,
        context,
        propertyDelegate
    )

    constructor(syncId: Int, playerInventory: Inventory) : super(
        ModScreenHandlerTypes.VAC_PIPE_STATION_SEND_SCREEN_HANDLER,
        syncId,
        playerInventory,
    )

    override fun addVariantSlots(inventory: Container) {
        addSlot(Slot(inventory, 0, 15, 21))
        addSlot(Slot(inventory, 1, 15, 51))
        addSlot(Slot(inventory, 2, 15, 76))
    }
}