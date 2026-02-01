package grauly.dustydecor.event

import grauly.dustydecor.ModAttachmentTypes
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModDamageTypes
import net.minecraft.world.effect.MobEffects
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.HitResult
import net.minecraft.world.level.ClipContext
import kotlin.math.max
import kotlin.math.min

object VoidGoopLookHandler {
    private const val MAX_DISTANCE = 50.0
    private const val CONSUMPTION_TIME_TICKS: Int = 7 * 20
    private const val CONSUMPTION_TICK_INCREMENT: Float = 1f / CONSUMPTION_TIME_TICKS
    private const val ATTENUATE_MULTIPLIER: Float = 1f / 8

    fun onEndTick(serverLevel: ServerLevel) {
        serverLevel.players().forEach { player ->
            val isLooking = isLookingAtGoop(player, serverLevel)
            val previous = player.modifyAttached(ModAttachmentTypes.VOID_CONSUMPTION) {
                val value = it ?: 0f
                if (isLooking) {
                    min(1f, value + CONSUMPTION_TICK_INCREMENT)
                } else {
                    max(
                        0f,
                        value - CONSUMPTION_TICK_INCREMENT * if(player.gameMode().isSurvival) ATTENUATE_MULTIPLIER else 1f
                    )
                }
            }
            if ((previous ?: 0f) < 1f) return@forEach
            player.hurtServer(
                serverLevel,
                serverLevel.damageSources().source(ModDamageTypes.VOID_CONSUMPTION),
                5f
            )
        }
    }

    private fun isLookingAtGoop(player: ServerPlayer, world: ServerLevel): Boolean {
        if (player.gameMode.isSurvival) return false
        if (player.hasEffect(MobEffects.BLINDNESS)) return false
        val pos = player.getEyePosition(0f)
        val rotation = player.getViewVector(0f).normalize().scale(MAX_DISTANCE)
        val hitResult = world.clip(
            ClipContext(
                pos,
                pos.add(rotation),
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                player
            )
        )
        if (hitResult.type == HitResult.Type.MISS) return false
        val lookAtState = world.getBlockState(hitResult.blockPos)
        return lookAtState.`is`(ModBlocks.VOID_GOOP)
    }
}