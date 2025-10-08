package grauly.dustydecor.hud

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModAttachmentTypes
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier
import net.minecraft.util.math.ColorHelper

object VoidGoopOverlayRenderer {
    private val client = MinecraftClient.getInstance()
    private val TEXTURE = Identifier.of(DustyDecorMod.MODID, "textures/misc/void_overlay.png")

    fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        val player = client.player ?: return
        val consumption = player.getAttachedOrElse(ModAttachmentTypes.VOID_CONSUMPTION, 0f)
        val white = ColorHelper.getWhite(consumption)
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            0, 0,
            0f, 0f,
            context.scaledWindowWidth,
            context.scaledWindowHeight,
            context.scaledWindowWidth,
            context.scaledWindowHeight,
            white
        )
    }
}