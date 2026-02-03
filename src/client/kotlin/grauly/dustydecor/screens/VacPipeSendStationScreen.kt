package grauly.dustydecor.screens

import grauly.dustydecor.screen.VacPipeSendStationScreenHandler
import net.minecraft.world.entity.player.Inventory
import net.minecraft.network.chat.Component

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