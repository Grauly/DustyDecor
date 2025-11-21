package grauly.dustydecor.screens

import grauly.dustydecor.screen.VacPipeReceiveStationScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class VacPipeReceiveStationScreen(
    handler: VacPipeReceiveStationScreenHandler,
    inventory: PlayerInventory?,
    title: Text?
) : VacPipeStationScreen<VacPipeReceiveStationScreenHandler>(
    handler,
    inventory,
    title,
    RECEIVE_TEXTURE
) {
}