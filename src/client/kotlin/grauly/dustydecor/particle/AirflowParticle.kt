package grauly.dustydecor.particle

import net.minecraft.client.Camera
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
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    private val flowDirection: Direction,
    sprites: SpriteSet,
    upwardsAcceleration: Float
) : FixedRotationOffsetParticle(level, x, y, z, sprites, upwardsAcceleration) {
    private val axisRotationRadians: Float = level.random.nextFloat() * 2 * PI.toFloat()

    init {
        lifetime = 11
    }

    override fun getOffset(camera: Camera, tickProgress: Float): Vector3f = Vector3f(4 / 16f, -3 / 16f, 0f)

    override fun getRotation(camera: Camera, tickProgress: Float): Quaternionf =
        Quaternionf()
            .rotateTo(Direction.UP.unitVec3f, flowDirection.opposite.unitVec3f)
            .rotateY(axisRotationRadians)

    class InflowProvider(private val sprites: SpriteSet) : ParticleProvider<AirInflowParticleOptions> {
        override fun createParticle(
            options: AirInflowParticleOptions,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle {
            return AirflowParticle(
                level,
                x, y, z,
                options.inflowDirection.opposite,
                sprites,
                0.0f
            )
        }
    }

    class OutflowProvider(private val sprites: SpriteSet) : ParticleProvider<AirOutflowParticleOptions> {
        override fun createParticle(
            options: AirOutflowParticleOptions,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle {
            return AirflowParticle(
                level,
                x, y, z,
                options.outflowDirection,
                sprites,
                0.0f
            )
        }
    }
}