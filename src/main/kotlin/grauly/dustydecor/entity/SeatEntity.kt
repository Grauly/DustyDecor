package grauly.dustydecor.entity

import com.mojang.serialization.Codec
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
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.DismountHelper
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import kotlin.math.floor

class SeatEntity(private var linkedLocation: BlockPos, private var isLocationLinked: Boolean, type: EntityType<*>, world: Level) : Entity(type, world) {

    constructor(type: EntityType<*>, world: Level) : this(BlockPos.ZERO, false, type, world)

    override fun tick() {
        super.tick()
        if (!isLocationLinked) return
        if (!BlockPos.containing(position()).equals(linkedLocation)) {
            discard()
            return
        }
        if (level().getBlockState(linkedLocation).block !is SeatLinkable) {
            discard()
            return
        }
        if (!isVehicle) {
            discard()
            return
        }
    }

    override fun positionRider(
        passenger: Entity,
        moveFunction: MoveFunction
    ) {
        super.positionRider(passenger, moveFunction)
        if (passenger !is Player) return
        passenger.yBodyRot = passenger.yRot
    }

    override fun hurtServer(
        world: ServerLevel,
        source: DamageSource,
        amount: Float
    ): Boolean {
        return false
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        // [Space intentionally left blank]
    }

    override fun readAdditionalSaveData(view: ValueInput) {
        linkedLocation = view.read(LINKED_LOCATION_KEY, BlockPos.CODEC).orElse(BlockPos.ZERO)
        isLocationLinked = view.read(LOCATION_LINKED_KEY, Codec.BOOL).orElse(true)
    }

    override fun addAdditionalSaveData(view: ValueOutput) {
        view.store(LOCATION_LINKED_KEY, Codec.BOOL, isLocationLinked)
        view.store(LINKED_LOCATION_KEY, BlockPos.CODEC, linkedLocation)
    }

    override fun getDismountLocationForPassenger(passenger: LivingEntity): Vec3 {
        if (passenger !is Player) return defaultDismount(passenger)
        val from = passenger.eyePosition
        val to = passenger.eyePosition.add(passenger.headLookAngle.normalize().scale(2.0))
        val result = level().clip(ClipContext(
            from,
            to,
            ClipContext.Block.VISUAL,
            ClipContext.Fluid.NONE,
            passenger
        ))
        if (result.type == HitResult.Type.MISS) return defaultDismount(passenger)
        if (result.type == HitResult.Type.ENTITY) return defaultDismount(passenger)
        if (result.blockPos.equals(linkedLocation)) return position().add(.0, .001, .0)
        return defaultDismount(passenger)
    }

    private fun defaultDismount(passenger: LivingEntity): Vec3 {
        val dismountVector = passenger.headLookAngle.multiply(1.0, 0.0, 1.0).normalize()
        val absoluteTargetPosition = position().add(dismountVector)
        val adjustedTargetPosition = Vec3(absoluteTargetPosition.x, floor(position().y), absoluteTargetPosition.z)
        val box = passenger.getLocalBoundsForPose(Pose.STANDING)
        if (DismountHelper.canDismountTo(level(), passenger, box.move(adjustedTargetPosition))) {
            return adjustedTargetPosition
        }
        return position().add(.0, .001, .0)
    }

    companion object {
        const val LINKED_LOCATION_KEY = "linkedLocation"
        const val LOCATION_LINKED_KEY = "isLocationLinked"
        fun seatEntity(world: Level, pos: BlockPos, offset: Vec3, entity: Entity): SitResult {
            if (world !is ServerLevel) return SitResult(SitResultType.NONE, null)
            if (world.getEntitiesOfClass(SeatEntity::class.java, AABB(pos), { _ -> true}).isNotEmpty()) return SitResult(SitResultType.OCCUPIED, null)
            if (entity.isPassenger) return SitResult(SitResultType.ALREADY_SITTING, null)
            if (entity.position().distanceToSqr(pos.center) >= 4) return SitResult(SitResultType.TOO_FAR, null)
            val seat = createLinked(world, pos, offset, entity)
            return SitResult(SitResultType.SUCCESS, seat)
        }

        fun createLinked(world: ServerLevel, pos: BlockPos, offset: Vec3, entity: Entity): SeatEntity {
            val seat = SeatEntity(pos, true, ModEntities.SEAT_ENTITY, world)
            seat.setPos(Vec3(pos).add(offset))
            world.addFreshEntity(seat)
            entity.isSprinting = false
            entity.startRiding(seat)
            return seat
        }
    }

    data class SitResult(val type: SitResultType, val seat: SeatEntity?)
}