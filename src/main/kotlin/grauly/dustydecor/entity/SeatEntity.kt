package grauly.dustydecor.entity

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.server.world.ServerWorld
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.world.World

class SeatEntity(type: EntityType<*>, world: World) : Entity(type, world) {
    override fun initDataTracker(builder: DataTracker.Builder?) {
        // [Space intentionally left blank]
    }

    override fun damage(
        world: ServerWorld?,
        source: DamageSource?,
        amount: Float
    ): Boolean {
        return false
    }

    override fun readCustomData(view: ReadView?) {
        // [Space intentionally left blank]
    }

    override fun writeCustomData(view: WriteView?) {
        // [Space intentionally left blank]
    }
}