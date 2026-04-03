package grauly.dustydecor.blockentity

import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.block.furniture.PhoneBlock
import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import grauly.dustydecor.particle.PhoneRingParticleOptions
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.math.max

class PhoneBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(ModBlockEntityTypes.PHONE_ENTITY, worldPosition, blockState), ItemOwner {
    var pauseTicks = INIT_RANDOM.nextInt(RING_PAUSE_TICKS)
    var ringTicks = -1
    val ringCycle = 4

    fun canRing(): Boolean {
        return blockState.getValue(PhoneBlock.RINGING) && blockState.getValue(PhoneBlock.ON_HOOK)
    }

    override fun level(): Level = level!!

    override fun position(): Vec3 = worldPosition.bottomCenter

    override fun getVisualRotationYInDegrees(): Float =
        -blockState.getValue(SingleFurnitureBlock.ROTATION) * (360f / 16f)

    companion object {
        const val RING_TICKS = 40
        const val RING_PAUSE_TICKS = 80

        val INIT_RANDOM = Random(System.currentTimeMillis())

        fun tick(level: Level, pos: BlockPos, state: BlockState, entity: PhoneBlockEntity) {
            if (entity.canRing()) {
                tickRing(level, pos, state, entity)
            }
        }

        fun tickRing(level: Level, pos: BlockPos, state: BlockState, entity: PhoneBlockEntity) {
            if (entity.ringTicks % 8 == 0) {
                level.addParticle(
                    PhoneRingParticleOptions(
                        true,
                        entity.ringTicks % 16 == 0
                    ),
                    entity.position().x,
                    entity.position().y,
                    entity.position().z,
                    0.0, 0.0, 0.0
                )
            }
            if (entity.ringTicks > 0) entity.ringTicks = max(entity.ringTicks - 1, 0)
            if (entity.ringTicks == 0) {
                entity.pauseTicks = RING_PAUSE_TICKS
                entity.ringTicks = -1
            }
            if (entity.pauseTicks == 0) {
                entity.ringTicks = RING_TICKS
                entity.pauseTicks = -1
                level.playLocalSound(
                    pos,
                    ModSoundEvents.PHONE_RING_A,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f,
                    false
                )
                level.playLocalSound(
                    pos,
                    ModSoundEvents.PHONE_RING_FAR_A,
                    SoundSource.BLOCKS,
                    3.0f,
                    1.0f,
                    false
                )
            }
            if (entity.pauseTicks > 0) entity.pauseTicks = max(entity.pauseTicks - 1, 0)
        }
    }
}