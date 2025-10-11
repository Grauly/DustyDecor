package grauly.dustydecor.hud

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModAttachmentTypes
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier
import net.minecraft.util.math.ColorHelper
import kotlin.math.*

object VoidGoopOverlayRenderer {
    private val client = MinecraftClient.getInstance()
    private val END_TEXTURE = Identifier.of(DustyDecorMod.MODID, "textures/misc/void_overlay/void_streak_end.png")
    private val TEXTURE = Identifier.of(DustyDecorMod.MODID, "textures/misc/void_overlay/void_streak.png")

    //FIXME: Performance is suboptimal, as is to be expected from drawing a shitton of elements

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
                val y = getOffset(context, easing(consumption), x)
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
                4, 16,
                if (client.player?.gameMode?.isSurvivalLike == true) -1 else ColorHelper.getWhite(0.2f)
            )
        }
    }

    private fun getOffset(context: DrawContext, delta: Float, x: Int): Int {
        val eyeprogress = max(0f, min(1f, (2 - delta * 2)))
        val offset = max(0f, min(1f, 1 - delta * 2))
        return ((sin(PI * x * (1f / context.scaledWindowWidth)).pow(2.0 + (1 - eyeprogress)) * .5 * eyeprogress * context.scaledWindowHeight + .5f * (1 + offset) * context.scaledWindowHeight).roundToInt())
    }

    private fun easing(delta: Float): Float {
        return 1f - (1f - delta).pow(3)
    }
}