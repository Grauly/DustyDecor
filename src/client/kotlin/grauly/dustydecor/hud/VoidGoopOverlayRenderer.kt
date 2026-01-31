package grauly.dustydecor.hud

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModAttachmentTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.DeltaTracker
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ARGB
import kotlin.math.*

object VoidGoopOverlayRenderer {
    private val client = Minecraft.getInstance()
    private val END_TEXTURE = ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, "textures/misc/void_overlay/void_streak_end.png")
    private val TEXTURE = ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, "textures/misc/void_overlay/void_streak.png")

    private val CREATIVE_COLOR = ARGB.white(0.5f)
    private val SURVIVAL_COLOR = ARGB.white(1f)

    private const val ELEMENT_WIDTH = 4
    private const val ELEMENT_HEIGHT = 16

    fun render(context: GuiGraphics, tickCounter: DeltaTracker) {
        val player = client.player ?: return
        val consumption = player.getAttachedOrElse(ModAttachmentTypes.VOID_CONSUMPTION, 0f)
        if (consumption <= 0.0) return
        val delta = easing(consumption)
        val gridSize = ELEMENT_WIDTH
        val gridPoints = context.guiWidth() / gridSize + 1
        context.pose().pushMatrix()
        for (n in 0..1) {
            if (n == 1) {
                context.pose().translate(
                    context.guiWidth().toFloat(),
                    context.guiHeight().toFloat()
                )
                context.pose().rotate(PI.toFloat())
            }
            drawBlackout(context, delta)
            for (i in 1..gridPoints) {
                val x = -(ELEMENT_WIDTH / 2) + (i * ELEMENT_WIDTH)
                val y = getTopY(context, delta, x)

                context.blit(
                    RenderPipelines.GUI_TEXTURED,
                    END_TEXTURE,
                    x - ELEMENT_WIDTH, y - ELEMENT_HEIGHT,
                    0f, 0f, ELEMENT_WIDTH, ELEMENT_HEIGHT,
                    ELEMENT_WIDTH, ELEMENT_HEIGHT,
                    if (client.player?.gameMode()?.isSurvival == true) SURVIVAL_COLOR else CREATIVE_COLOR
                )
            }
        }
        context.pose().popMatrix()
    }

    private fun drawBlackout(context: GuiGraphics, delta: Float) {
        val localMinY = 0
        val localMaxY = getTopY(context, delta, context.guiWidth() / 2) - ELEMENT_HEIGHT / 2
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            0, localMinY,
            0f, 0f,
            context.guiWidth(), localMaxY - localMinY,
            context.guiWidth(), localMaxY - localMinY,
            if (client.player?.gameMode()?.isSurvival == true) SURVIVAL_COLOR else CREATIVE_COLOR
        )
        var diff = context.guiWidth() / 4
        var lastDiff = 0
        while (diff >= ELEMENT_WIDTH) {
            drawBlackoutRect(context, delta, lastDiff, lastDiff + diff, localMaxY)
            lastDiff += diff
            diff /= 2
        }
    }

    private fun drawBlackoutRect(context: GuiGraphics, delta: Float, fromX: Int, toX: Int, minY: Int) {
        if (toX - fromX < 4) return
        val maxY =
            min(getTopY(context, delta, fromX) - ELEMENT_HEIGHT / 4, getTopY(context, delta, toX) - ELEMENT_HEIGHT / 4)
        if (minY == maxY) return
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            fromX, minY,
            0f, 0f,
            toX - fromX, maxY - minY,
            toX - fromX, maxY - minY,
            if (client.player?.gameMode()?.isSurvival == true) SURVIVAL_COLOR else CREATIVE_COLOR
        )
        //mirror universe shenanigans
        val mirrorFromX = context.guiWidth() / 2 + (context.guiWidth() / 2 - toX)
        val mirrorToX = context.guiWidth() / 2 + (context.guiWidth() / 2 - fromX)
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            mirrorFromX, minY,
            0f, 0f,
            mirrorToX - mirrorFromX, maxY - minY,
            mirrorToX - mirrorFromX, maxY - minY,
            if (client.player?.gameMode()?.isSurvival == true) SURVIVAL_COLOR else CREATIVE_COLOR
        )
        var diff = (toX - fromX) / 2
        var lastDiff = 0
        while (diff >= ELEMENT_WIDTH) {
            drawBlackoutRect(context, delta, fromX + lastDiff, fromX + lastDiff + diff, maxY)
            lastDiff += diff
            diff /= 2
        }

    }

    private fun getTopY(context: GuiGraphics, delta: Float, x: Int): Int {
        return -getOffset(context, delta, x) + context.guiHeight()
    }

    private fun getOffset(context: GuiGraphics, delta: Float, x: Int): Int {
        val safeX = min(context.guiWidth(), max(x, 0))
        val eyeProgress = max(0f, min(1f, (2 - delta * 2)))
        val offset = max(0f, min(1f, 1 - delta * 2))
        val baseSine = max(0.0, (sin(PI * safeX * (1f / context.guiWidth()))))
        val power = (2.0 + (1 - eyeProgress))
        val blinkFactor = .5 * eyeProgress * context.guiHeight()
        val offsetFactor = .5 * (1 + offset) * context.guiHeight()
        val res = baseSine.pow(power) * blinkFactor + offsetFactor
        try {
            return floor(res).roundToInt()
        } catch (e: Exception) {
            DustyDecorMod.logger.info("Caught Exception while calculating eye function: $e")
            DustyDecorMod.logger.info("$res @ x=$x safeX=$safeX, eyeProgress=$eyeProgress offset=$offset scaledWidth=${context.guiWidth()} scaledHeight=${context.guiHeight()}")
            DustyDecorMod.logger.info("(($baseSine)^$power) * $blinkFactor + $offsetFactor")
            DustyDecorMod.logger.info("${baseSine.pow(power)} * ${blinkFactor + offsetFactor}")
            return 0
        }
    }

    private fun easing(delta: Float): Float {
        return 1f - (1f - delta).pow(2)
    }
}