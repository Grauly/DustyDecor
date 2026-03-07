package grauly.dustydecor.screens

import grauly.dustydecor.screen.VacPipeReceiveStationScreenHandler
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class VacPipeReceiveStationScreen(
    handler: VacPipeReceiveStationScreenHandler,
    inventory: Inventory,
    title: Component
) : VacPipeStationScreen<VacPipeReceiveStationScreenHandler>(
    handler,
    inventory,
    title,
    RECEIVE_TEXTURE
) {
}