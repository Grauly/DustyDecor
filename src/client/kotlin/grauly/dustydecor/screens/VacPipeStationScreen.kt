package grauly.dustydecor.screens

import grauly.dustydecor.screen.VacPipeStationScreenHandler
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class VacPipeStationScreen(handler: VacPipeStationScreenHandler?, inventory: PlayerInventory?, title: Text?) :
    HandledScreen<VacPipeStationScreenHandler>(handler, inventory, title) {
    override fun drawBackground(context: DrawContext, deltaTicks: Float, mouseX: Int, mouseY: Int) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0f, 0f, backgroundWidth, backgroundHeight, 256, 256)
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        super.render(context, mouseX, mouseY, deltaTicks)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    companion object {
        private val TEXTURE = Identifier.ofVanilla("textures/gui/container/shulker_box.png")
    }
}