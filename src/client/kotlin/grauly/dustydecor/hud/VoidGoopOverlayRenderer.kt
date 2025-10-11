package grauly.dustydecor.hud

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModAttachmentTypes
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier
import kotlin.math.*

object VoidGoopOverlayRenderer {
    private val client = MinecraftClient.getInstance()
    private val END_TEXTURE = Identifier.of(DustyDecorMod.MODID, "textures/misc/void_overlay/void_streak_end.png")
    private val TEXTURE = Identifier.of(DustyDecorMod.MODID, "textures/misc/void_overlay/void_streak.png")

    fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        val player = client.player ?: return
        val consumption = player.getAttachedOrElse(ModAttachmentTypes.VOID_CONSUMPTION, 0f)
        if (consumption <= 0.0) return
        val gridSize = 4
        val gridPoints = context.scaledWindowWidth / gridSize
        context.matrices.pushMatrix()
        for (n in 0..1) {
            if (n == 1) {
                context.matrices.translate(
                    context.scaledWindowWidth.toFloat(),
                    context.scaledWindowHeight.toFloat()
                )
                context.matrices.rotate(PI.toFloat())
            }
            for (i in 1..gridPoints) {
                val x = -2 + i * gridSize
                val y = getOffset(context, consumption, x)
                drawStreak(context, -y + context.scaledWindowHeight, x)
            }
        }
        context.matrices.popMatrix()
    }

    private fun drawStreak(context: DrawContext, targetY: Int, centerX: Int) {
        val segmentCount: Int = floor(targetY / (8 * 2f)).toInt()
        for (i in 0..segmentCount) {
            context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                if (i == 0) END_TEXTURE else TEXTURE,
                centerX - 2, targetY - (i + 1) * 8 * 2,
                0f, 0f, 4, 16,
                4, 16
            )
        }
    }

    private fun getOffset(context: DrawContext, delta: Float, x: Int): Int {
        val eyeprogress = max(0f, min(1f, (2 - delta * 2)))
        val offset = max(0f, min(1f, 1 - delta * 2))
        return ((sin(PI * x * (1f / context.scaledWindowWidth)).pow(2.0 + (1 - eyeprogress)) * .5 * eyeprogress * context.scaledWindowHeight + .5f * (1 + offset) * context.scaledWindowHeight).roundToInt())
    }
}