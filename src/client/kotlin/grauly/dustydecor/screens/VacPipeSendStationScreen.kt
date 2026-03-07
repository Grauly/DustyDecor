package grauly.dustydecor.screens

import grauly.dustydecor.screen.VacPipeSendStationScreenHandler
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class VacPipeSendStationScreen(
    handler: VacPipeSendStationScreenHandler,
    inventory: Inventory,
    title: Component
) : VacPipeStationScreen<VacPipeSendStationScreenHandler>(
    handler,
    inventory,
    title,
    SEND_TEXTURE
) {
}