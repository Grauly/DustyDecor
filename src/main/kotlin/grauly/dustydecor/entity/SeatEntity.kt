package grauly.dustydecor.entity

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModEntities
import grauly.dustydecor.block.furniture.SeatLinkable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level

class SeatEntity(type: EntityType<*>, world: Level) : Entity(type, world) {
    private var linkedLocation: BlockPos? = null

    override fun tick() {
        super.tick()
        if (linkedLocation == null) return
        if (!BlockPos.containing(position()).equals(linkedLocation)) {
            discard()
            return
        }
        if (level().getBlockState(linkedLocation).block !is SeatLinkable) {
            discard()
            return
        }
        if (!hasExactlyOnePlayerPassenger()) {
            discard()
            return
        }
    }

    override fun hurtServer(
        world: ServerLevel?,
        source: DamageSource?,
        amount: Float
    ): Boolean {
        return false
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder?) {
        // [Space intentionally left blank]
    }

    override fun readAdditionalSaveData(view: ValueInput) {
        linkedLocation = view.read(LINKED_LOCATION_KEY, BlockPos.CODEC).orElse(null)
    }

    override fun addAdditionalSaveData(view: ValueOutput) {
        if (linkedLocation != null) {
            view.store(LINKED_LOCATION_KEY, BlockPos.CODEC, linkedLocation)
        }
    }

    companion object {
        const val LINKED_LOCATION_KEY = "linkedLocation"
        fun createLinked(world: Level, pos: BlockPos, offset: Vec3, entity: Entity): SeatEntity? {
            if (world !is ServerLevel) return null
            if (world.getEntitiesOfClass(SeatEntity::class.java, AABB(pos), { _ -> true}).isNotEmpty()) return null
            val seat = SeatEntity(ModEntities.SEAT_ENTITY, world)
            seat.setPos(Vec3(pos).add(offset))
            seat.linkedLocation = pos
            world.addFreshEntity(seat)
            entity.isSprinting = false
            entity.startRiding(seat)
            return seat
        }
    }
}