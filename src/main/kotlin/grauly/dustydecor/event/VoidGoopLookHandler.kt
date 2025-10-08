package grauly.dustydecor.event

import grauly.dustydecor.ModAttachmentTypes
import grauly.dustydecor.ModBlocks
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext
import kotlin.math.max
import kotlin.math.min

object VoidGoopLookHandler {
    private const val MAX_DISTANCE = 50.0
    private const val CONSUMPTION_TIME_TICKS: Int = 30 * 20
    private const val CONSUMPTION_TICK_INCREMENT: Float = 1f / CONSUMPTION_TIME_TICKS
    private const val ATTENUATE_MULTIPLIER: Float = 1f / 2

    fun onEndTick(serverWorld: ServerWorld) {
        serverWorld.players.forEach { player ->
            val isLooking = isLookingAtGoop(player, serverWorld)
            player.modifyAttached(ModAttachmentTypes.VOID_CONSUMPTION) {
                val value = it ?: 0f
                if (isLooking) {
                    min(1f, value + CONSUMPTION_TICK_INCREMENT)
                } else {
                    max(0f, value - CONSUMPTION_TICK_INCREMENT * ATTENUATE_MULTIPLIER)
                }
            }
        }
    }

    private fun isLookingAtGoop(player: ServerPlayerEntity, world: ServerWorld): Boolean {
        if (!player.gameMode.isSurvivalLike) return false
        if (player.hasStatusEffect(StatusEffects.BLINDNESS)) return false
        val pos = player.getCameraPosVec(0f)
        val rotation = player.getRotationVec(0f).normalize().multiply(MAX_DISTANCE)
        val hitResult = world.raycast(
            RaycastContext(
                pos,
                pos.add(rotation),
                RaycastContext.ShapeType.VISUAL,
                RaycastContext.FluidHandling.NONE,
                player
            )
        )
        if (hitResult.type == HitResult.Type.MISS) return false
        val lookAtState = world.getBlockState(hitResult.blockPos)
        return lookAtState.isOf(ModBlocks.VOID_GOOP)
    }
}