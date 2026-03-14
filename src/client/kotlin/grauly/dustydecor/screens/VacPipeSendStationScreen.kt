package grauly.dustydecor.screens

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.blockentity.vac_station.SendMode
import grauly.dustydecor.screen.VacPipeSendStationScreenHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class VacPipeSendStationScreen(
    private val handler: VacPipeSendStationScreenHandler,
    inventory: Inventory,
    title: Component
) : VacPipeStationScreen<VacPipeSendStationScreenHandler>(
    handler,
    inventory,
    title,
    SEND_TEXTURE
) {
    private lateinit var sendButton: Button

    override fun init() {
        super.init()
        sendButton = addRenderableWidget(Button.builder(
            Component.translatable(SEND_BUTTON_TEXT),
            { button ->
                Minecraft.getInstance().gameMode?.handleInventoryButtonClick(handler.containerId, 30 + 1)
            }
        ).pos(leftPos + 36, topPos + 74)
            .size(38, 18)
            .build()
        )
    }

    override fun containerTick() {
        super.containerTick()
        sendButton.active = handler.getSendingMode() == SendMode.MANUAL
    }

    companion object {
        const val SEND_BUTTON_TEXT = "screen.${DustyDecorMod.MODID}.vap_pipe_station.button.send"
    }
}