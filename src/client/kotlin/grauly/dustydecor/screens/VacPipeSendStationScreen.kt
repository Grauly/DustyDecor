package grauly.dustydecor.screens

import grauly.dustydecor.screen.VacPipeSendStationScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class VacPipeSendStationScreen(
    handler: VacPipeSendStationScreenHandler,
    inventory: PlayerInventory?,
    title: Text?
) : VacPipeStationScreen<VacPipeSendStationScreenHandler>(
    handler,
    inventory,
    title,
    SEND_TEXTURE
) {
}