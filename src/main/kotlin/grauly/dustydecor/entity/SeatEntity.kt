package grauly.dustydecor.entity

import grauly.dustydecor.ModEntities
import grauly.dustydecor.block.furniture.SeatLinkable
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.server.world.ServerWorld
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class SeatEntity(type: EntityType<*>, world: World) : Entity(type, world) {
    private var linkedLocation: BlockPos? = null

    override fun tick() {
        super.tick()
        if (linkedLocation == null) return
        if (!BlockPos.ofFloored(trackedPosition.pos).equals(linkedLocation)) {
            discard()
            return
        }
        if (entityWorld.getBlockState(linkedLocation).block !is SeatLinkable) {
            discard()
            return
        }
        if (!hasPlayerRider()) {
            discard()
            return
        }
    }

    override fun damage(
        world: ServerWorld?,
        source: DamageSource?,
        amount: Float
    ): Boolean {
        return false
    }

    override fun initDataTracker(builder: DataTracker.Builder?) {
        // [Space intentionally left blank]
    }

    override fun readCustomData(view: ReadView) {
        linkedLocation = view.read(LINKED_LOCATION_KEY, BlockPos.CODEC).orElse(null)
    }

    override fun writeCustomData(view: WriteView) {
        if (linkedLocation != null) {
            view.put(LINKED_LOCATION_KEY, BlockPos.CODEC, linkedLocation)
        }
    }

    companion object {
        const val LINKED_LOCATION_KEY = "linkedLocation"
        fun createLinked(world: World, pos: BlockPos, offset: Vec3d, entity: Entity): SeatEntity? {
            if (world !is ServerWorld) return null
            if (world.getEntitiesByClass(SeatEntity::class.java, Box(pos), { _ -> true}).isNotEmpty()) return null
            val seat = SeatEntity(ModEntities.SEAT_ENTITY, world)
            seat.setPosition(Vec3d(pos).add(offset))
            world.spawnEntity(seat)
            entity.isSprinting = false
            entity.startRiding(seat)
            return seat
        }
    }
}