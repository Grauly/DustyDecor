package grauly.dustydecor.screens

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.screen.VacPipeStationScreenHandler
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier

abstract class  VacPipeStationScreen <T: VacPipeStationScreenHandler<*>>(
    handler: T,
    inventory: PlayerInventory?,
    title: Text?,
    private val texture: Identifier,
) : HandledScreen<T>(handler, inventory, title) {
    init {
        backgroundWidth = 176
        backgroundHeight = 189
        playerInventoryTitleY = this.backgroundHeight - 94
    }

    override fun drawBackground(context: DrawContext, deltaTicks: Float, mouseX: Int, mouseY: Int) {
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            texture,
            x,
            y,
            0f,
            0f,
            backgroundWidth,
            backgroundHeight,
            256,
            256
        )
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        super.render(context, mouseX, mouseY, deltaTicks)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    companion object {
        val SEND_TEXTURE: Identifier =
            Identifier.of(DustyDecorMod.MODID, "textures/gui/container/vac_pipe_station_send.png")
        val RECEIVE_TEXTURE: Identifier =
            Identifier.of(DustyDecorMod.MODID, "textures/gui/container/vac_pipe_station_receive.png")
    }
}