package grauly.dustydecor.blockentity

import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModParticleTypes
import grauly.dustydecor.block.LightingFixtureBlock
import grauly.dustydecor.util.DyeUtils
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.random.Random

class AlarmCageLampBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(
    ModBlockEntityTypes.ALARM_CAGE_LAMP_ENTITY,
    pos,
    state
) {
    var age: Int = 0
    var color: Int = 0

    init {
        age += RANDOM.nextInt(20)
    }

    fun shouldShowBeams(): Boolean {
        if (cachedState.get(LightingFixtureBlock.BROKEN)) return false
        return cachedState.get(LightingFixtureBlock.LIT) != cachedState.get(LightingFixtureBlock.INVERTED)
    }

    fun getRotationDirection(): Vec3d {
        return cachedState.get(Properties.FACING).doubleVector
    }

    fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: AlarmCageLampBlockEntity) {
        //I hate how tickDelta (or now tickProgress) works. I would not have to tick this if it just worked normally
        age++
        if (color == 0) {
            color = DyeUtils.COLOR_ORDER[ModBlocks.ALARM_CAGE_LAMPS.indexOf(world.getBlockState(pos).block)].signColor
        }
        if (world.isClient) return
        world as ServerWorld
        val lightOrigin = getRotationDirection().multiply(-3 / 16.0)
        val validBlindingPlayers =
            world.players.filter { it.entityPos.squaredDistanceTo(lightOrigin) <= BLINDING_DISTANCE * BLINDING_DISTANCE }
        if (validBlindingPlayers.isEmpty()) return
        val minRotation = Quaternionf()
            .rotateTo(Vector3f(0f, 1f, 0f), getRotationDirection().toVector3f())
            .mul(Quaternionf().rotateY(age.toFloat()))
        val maxRotation = Quaternionf()
            .rotateTo(Vector3f(0f, 1f, 0f), getRotationDirection().toVector3f())
            .mul(Quaternionf().rotateY((age + 1).toFloat()))
        val workingRotation = minRotation.nlerp(maxRotation, 0.5f)
        val lightVector = Vector3f(1f, 0f, 0f).rotate(workingRotation)
        validBlindingPlayers.forEach {
            val lightToEyes = it.eyePos.subtract(lightOrigin).normalize()
            if (lightVector.distanceSquared(lightToEyes.toVector3f()) <= BLINDING_THRESHOLD * BLINDING_THRESHOLD) {
                val particleSpawn = Vec3d(Vector3f(lightVector).mul(5 / 16f).add(lightOrigin.toVector3f()))
                world.spawnParticles(
                    it,
                    ModParticleTypes.LIGHT_FLASH,
                    false, false,
                    particleSpawn.x, particleSpawn.y, particleSpawn.z,
                    0,
                    0.0, 0.0, 0.0,
                    1.0
                )
            }
        }
    }

    companion object {
        private val RANDOM = Random.Default
        private const val BLINDING_THRESHOLD = 0.04
        private const val BLINDING_DISTANCE = 6.0
    }
}