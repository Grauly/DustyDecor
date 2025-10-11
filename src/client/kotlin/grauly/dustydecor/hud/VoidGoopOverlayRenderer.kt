package grauly.dustydecor.hud

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModAttachmentTypes
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier
import org.joml.Vector2i
import kotlin.math.sqrt

object VoidGoopOverlayRenderer {
    private val client = MinecraftClient.getInstance()
    private val TEXTURE = Identifier.of(DustyDecorMod.MODID, "textures/misc/void_overlay/void_bubble.png")
    private val FULL_TEXTURE = Identifier.of(DustyDecorMod.MODID, "textures/misc/void_overlay/void_bubble_full.png")

    fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        val player = client.player ?: return
        val consumption = player.getAttachedOrElse(ModAttachmentTypes.VOID_CONSUMPTION, 0f)
        if (consumption <= 0.0) return
        val gridSize = 30
        val gridPointsX = ensureUneven((context.scaledWindowWidth / gridSize) + 1)
        val gridPointsY = ensureUneven((context.scaledWindowHeight / gridSize) + 1)
        val gridHalfX = (gridPointsX - 1) / 2
        val gridHalfY = (gridPointsY - 1) / 2
        val pointList = mutableListOf<Vector2i>()
        for (x in (-gridHalfX)..gridHalfX) {
            for (y in (-gridHalfY)..gridHalfY) {
                val gridX = context.scaledWindowWidth / 2 + (x * gridSize)
                val gridY = context.scaledWindowHeight / 2 + (y * gridSize)
                pointList.add(Vector2i(gridX, gridY))
                pointList.add(Vector2i(gridX + gridSize / 2, gridY + gridSize / 2))
            }
        }
        val center = Vector2i(context.scaledWindowWidth / 2, context.scaledWindowHeight / 2)
        val fullDistance = center.distance(Vector2i(0, 0)) * 1.1
        val fullAdjustedDistance = adjustedDistance(context, Vector2i(0, 0)) * 1.1
        pointList.removeAll {
            adjustedDistance(context, it) < fullAdjustedDistance * (1 - consumption)
        }
        //pointList.sortBy { it.distance(center) }
        pointList.forEach {
            val texture = if (
                adjustedDistance(context, it) < fullAdjustedDistance * (1 - consumption + 0.05)
                //it.distance(center) < fullDistance * (1 - consumption + 0.05)
                ) {
                FULL_TEXTURE
            } else TEXTURE
            drawCenteredBubble(context,texture, it.x, it.y)
        }
    }

    private fun drawCenteredBubble(context: DrawContext, texture: Identifier, centerX: Int, centerY: Int) {
        drawBubble(context, texture, centerX - 15, centerY - 15)
    }

    private fun drawBubble(context: DrawContext, texture: Identifier, x: Int, y: Int) {
        //yes, 32x32 instead of 16x16, to better match the vanilla overlays
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            texture,
            x, y,
            0f, 0f,
            30, 30, 32, 32,
            -1
        )
    }

    private fun adjustedDistance(context: DrawContext, vector: Vector2i): Double {
        val ratio = context.scaledWindowHeight / context.scaledWindowWidth.toDouble()
        val a = vector.x - context.scaledWindowWidth / 2.0
        val b = vector.y - context.scaledWindowHeight / 2.0
        return sqrt(a * a * ratio + b * b)
    }

    private fun ensureUneven(number: Int): Int {
        if (number % 2 == 1) return number
        return number + 1
    }
}