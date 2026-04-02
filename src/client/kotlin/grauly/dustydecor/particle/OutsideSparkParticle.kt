package grauly.dustydecor.particle

import grauly.dustydecor.ModParticleTypes
import grauly.dustydecor.particle.MetalSparkParticle.Companion.randomDoubleBetween
import grauly.dustydecor.particle.spark.SparkParticle
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import kotlin.math.PI
import kotlin.math.withSign

class OutsideSparkParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    gravityVector: Vec3,
    lifetime: Int,
    drag: Double = 1.0,
    bounceFactor: Double = 0.6,
    lengthFactor: Float = 4f,
    sparkWidthPixels: Double = 1.0,
    sprites: SpriteSet
) : SparkParticle(
    level,
    x,
    y,
    z,
    velocityX,
    velocityY,
    velocityZ,
    gravityVector,
    lifetime,
    drag,
    bounceFactor,
    lengthFactor,
    sparkWidthPixels,
    sprites
) {
    override fun onBounce() {
        this.remove()
    }

    override fun remove() {
        super.remove()
        // A really cool hack, from: https://math.stackexchange.com/q/4112622
        val perpendicular = Vec3(
            velocity.z.withSign(velocity.x),
            velocity.z.withSign(velocity.y),
            -velocity.x.withSign(velocity.z) - velocity.y.withSign(velocity.z)
        ).toVector3f().normalize(.2f)
        val rot = Quaternionf().rotateAxis(
            (PI / 2f).toFloat() + (random.nextFloat() * PI).toFloat(),
            velocity.x.toFloat(),
            velocity.y.toFloat(),
            velocity.z.toFloat()
        )
        for (i in 0..3) {
            perpendicular.rotate(rot)
            level.addParticle(
                ModParticleTypes.OUTSIDE_SPARKLET,
                pos.x, pos.y, pos.z,
                perpendicular.x.toDouble(), perpendicular.y.toDouble(), perpendicular.z.toDouble()
            )
        }
    }

    class LargeSparkProvider(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            options: SimpleParticleType,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle {
            return OutsideSparkParticle(
                level,
                x,
                y,
                z,
                velocityX,
                velocityY,
                velocityZ,
                Vec3(0.0, randomDoubleBetween(level.random, 2.3, 2.4) * 0.04, 0.0),
                level.random.nextInt(10) + 50,
                lengthFactor = 2.5f,
                sprites = sprites
            )
        }
    }

    class SmallSparkProvider(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            options: SimpleParticleType,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle {
            return OutsideSparkParticle(
                level,
                x,
                y,
                z,
                velocityX,
                velocityY,
                velocityZ,
                Vec3(0.0, randomDoubleBetween(level.random, 0.4, 0.5) * 0.04, 0.0),
                level.random.nextInt(5) + 25,
                lengthFactor = 3.5f,
                sprites = sprites
            )
        }
    }
}