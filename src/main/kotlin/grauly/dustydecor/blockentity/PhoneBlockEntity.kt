package grauly.dustydecor.blockentity

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import grauly.dustydecor.particle.PhoneRingParticleEffect
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import kotlin.math.max

class PhoneBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(ModBlockEntityTypes.PHONE_ENTITY, worldPosition, blockState), ItemOwner {
    var pauseTicks = 0;
    var ringTicks = 0;
    val ringCycle = 4;

    override fun level(): Level = level!!

    override fun position(): Vec3 = worldPosition.bottomCenter

    override fun getVisualRotationYInDegrees(): Float =
        -blockState.getValue(SingleFurnitureBlock.ROTATION) * (360f / 16f)

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, entity: PhoneBlockEntity) {
            if (entity.ringTicks % 8 == 0) {
                level.addParticle(
                    PhoneRingParticleEffect(
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
                entity.pauseTicks = 3*20
                entity.ringTicks = -1
            }
            if (entity.pauseTicks == 0) {
                entity.ringTicks = 24
                entity.pauseTicks = -1
                //TODO: play sound
            }
            if (entity.pauseTicks > 0) entity.pauseTicks = max(entity.pauseTicks - 1, 0)
        }
    }
}