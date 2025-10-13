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

    private val CREATIVE_COLOR = ColorHelper.getWhite(0.5f)
    private val SURVIVAL_COLOR = ColorHelper.getWhite(1f)

    private const val ELEMENT_WIDTH = 4
    private const val ELEMENT_HEIGHT = 16

    fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        val player = client.player ?: return
        val consumption = player.getAttachedOrElse(ModAttachmentTypes.VOID_CONSUMPTION, 0f)
        if (consumption <= 0.0) return
        val delta = easing(consumption)
        val gridSize = ELEMENT_WIDTH
        val gridPoints = context.scaledWindowWidth / gridSize + 1
        context.matrices.pushMatrix()
        for (n in 0..1) {
            if (n == 1) {
                context.matrices.translate(
                    context.scaledWindowWidth.toFloat(),
                    context.scaledWindowHeight.toFloat()
                )
                context.matrices.rotate(PI.toFloat())
            }
            drawBlackout(context, delta)
            for (i in 1..gridPoints) {
                val x = -(ELEMENT_WIDTH / 2) + (i * ELEMENT_WIDTH)
                val y = getTopY(context, delta, x)

                context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    END_TEXTURE,
                    x - ELEMENT_WIDTH, y - ELEMENT_HEIGHT,
                    0f, 0f, ELEMENT_WIDTH, ELEMENT_HEIGHT,
                    ELEMENT_WIDTH, ELEMENT_HEIGHT,
                    if (client.player?.gameMode?.isSurvivalLike == true) SURVIVAL_COLOR else CREATIVE_COLOR
                )
            }
        }
        context.matrices.popMatrix()
    }

    private fun drawBlackout(context: DrawContext, delta: Float) {
        val localMinY = 0
        val localMaxY = getTopY(context, delta, context.scaledWindowWidth / 2) - ELEMENT_HEIGHT / 2
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            0, localMinY,
            0f, 0f,
            context.scaledWindowWidth, localMaxY - localMinY,
            context.scaledWindowWidth, localMaxY - localMinY,
            if (client.player?.gameMode?.isSurvivalLike == true) SURVIVAL_COLOR else CREATIVE_COLOR
        )
        var diff = context.scaledWindowWidth / 4
        var lastDiff = 0
        while (diff >= ELEMENT_WIDTH) {
            drawBlackoutRect(context, delta, lastDiff, lastDiff + diff, localMaxY)
            lastDiff += diff
            diff /= 2
        }
    }

    private fun drawBlackoutRect(context: DrawContext, delta: Float, fromX: Int, toX: Int, minY: Int) {
        if (toX - fromX < 4) return
        val maxY =
            min(getTopY(context, delta, fromX) - ELEMENT_HEIGHT / 4, getTopY(context, delta, toX) - ELEMENT_HEIGHT / 4)
        if (minY == maxY) return
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            fromX, minY,
            0f, 0f,
            toX - fromX, maxY - minY,
            toX - fromX, maxY - minY,
            if (client.player?.gameMode?.isSurvivalLike == true) SURVIVAL_COLOR else CREATIVE_COLOR
        )
        //mirror universe shenanigans
        val mirrorFromX = context.scaledWindowWidth / 2 + (context.scaledWindowWidth / 2 - toX)
        val mirrorToX = context.scaledWindowWidth / 2 + (context.scaledWindowWidth / 2 - fromX)
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            mirrorFromX, minY,
            0f, 0f,
            mirrorToX - mirrorFromX, maxY - minY,
            mirrorToX - mirrorFromX, maxY - minY,
            if (client.player?.gameMode?.isSurvivalLike == true) SURVIVAL_COLOR else CREATIVE_COLOR
        )
        var diff = (toX - fromX) / 2
        var lastDiff = 0
        while (diff >= ELEMENT_WIDTH) {
            drawBlackoutRect(context, delta, fromX + lastDiff, fromX + lastDiff + diff, maxY)
            lastDiff += diff
            diff /= 2
        }

    }

    private fun getTopY(context: DrawContext, delta: Float, x: Int): Int {
        return -getOffset(context, delta, x) + context.scaledWindowHeight
    }

    private fun getOffset(context: DrawContext, delta: Float, x: Int): Int {
        val safeX = min(context.scaledWindowWidth, max(x, 0))
        val eyeProgress = max(0f, min(1f, (2 - delta * 2)))
        val offset = max(0f, min(1f, 1 - delta * 2))
        val baseSine = max(0.0, (sin(PI * safeX * (1f / context.scaledWindowWidth))))
        val power = (2.0 + (1 - eyeProgress))
        val blinkFactor = .5 * eyeProgress * context.scaledWindowHeight
        val offsetFactor = .5 * (1 + offset) * context.scaledWindowHeight
        val res = baseSine.pow(power) * blinkFactor + offsetFactor
        try {
            return res.roundToInt()
        } catch (e: Exception) {
            DustyDecorMod.logger.info("Caught Exception while calculating eye function: $e")
            DustyDecorMod.logger.info("$res @ x=$x safeX=$safeX, eyeProgress=$eyeProgress offset=$offset scaledWidth=${context.scaledWindowWidth} scaledHeight=${context.scaledWindowHeight}")
            DustyDecorMod.logger.info("(($baseSine)^$power) * $blinkFactor + $offsetFactor")
            DustyDecorMod.logger.info("${baseSine.pow(power)} * ${blinkFactor + offsetFactor}")
            return 0
        }
    }

    private fun easing(delta: Float): Float {
        return 1f - (1f - delta).pow(2)
    }
}