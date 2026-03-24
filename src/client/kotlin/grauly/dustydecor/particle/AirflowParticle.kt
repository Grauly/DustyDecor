package grauly.dustydecor.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

class AirflowParticle(
    world: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    private val flowDirection: Direction,
    spriteProvider: SpriteSet,
    upwardsAcceleration: Float
) : FixedRotationOffsetParticle(world, x, y, z, spriteProvider, upwardsAcceleration) {
    private val axisRotationRadians: Float = world.random.nextFloat() * 2 * PI.toFloat()

    init {
        lifetime = 11
    }

    override fun getOffset(): Vector3f = Vector3f(4 / 16f, -3 / 16f, 0f)

    override fun getRotation(): Quaternionf =
        Quaternionf()
            .rotateTo(Direction.UP.unitVec3f, flowDirection.opposite.unitVec3f)
            .rotateY(axisRotationRadians)

    class InflowFactory(private val spriteProvider: SpriteSet) : ParticleProvider<AirInflowParticleEffect> {
        override fun createParticle(
            parameters: AirInflowParticleEffect,
            world: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle {
            return AirflowParticle(
                world,
                x, y, z,
                parameters.inflowDirection.opposite,
                spriteProvider,
                0.0f
            )
        }
    }

    class OutflowFactory(private val spriteProvider: SpriteSet) : ParticleProvider<AirOutflowParticleEffect> {
        override fun createParticle(
            parameters: AirOutflowParticleEffect,
            world: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle {
            return AirflowParticle(
                world,
                x, y, z,
                parameters.outflowDirection,
                spriteProvider,
                0.0f
            )
        }
    }
}