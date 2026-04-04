package grauly.dustydecor.particle

import grauly.dustydecor.ModParticleTypes
import grauly.dustydecor.particle.spark.SparkParticle
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource
import kotlin.math.pow

class MetalSparkParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    gravity: Double,
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
    gravity,
    lifetime,
    drag,
    bounceFactor,
    lengthFactor,
    sparkWidthPixels,
    sprites
) {
    private var hasSplit = false

    override fun onBounce() {
        val randomNum = random.nextInt(10)
        if (randomNum < 2) {
            level.addParticle(
                ModParticleTypes.SPARK_FLASH,
                pos.x,
                pos.y,
                pos.z,
                velocity.x,
                velocity.y,
                velocity.z
            )
        }
        if (randomNum < 1) {
            split()
        }
    }

    fun split() {
        level.addParticle(ModParticleTypes.SPARK_FLASH, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z)
        if (!hasSplit) {
            val velocitySpread = velocity.length() * 0.6
            hasSplit = true
            level.addParticle(
                ModParticleTypes.SMALL_SPARK_PARTICLE,
                pos.x,
                pos.y,
                pos.z,
                velocity.x * 0.6f.pow(2) + random.nextFloat() * velocitySpread * 2 - velocitySpread,
                velocity.y * 0.6f.pow(2) + random.nextFloat() * velocitySpread * 2 - velocitySpread,
                velocity.z * 0.6f.pow(2) + random.nextFloat() * velocitySpread * 2 - velocitySpread,
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
            return MetalSparkParticle(
                level,
                x,
                y,
                z,
                velocityX,
                velocityY,
                velocityZ,
                randomDoubleBetween(level.random, 2.3, 2.4),
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
            return MetalSparkParticle(
                level,
                x,
                y,
                z,
                velocityX,
                velocityY,
                velocityZ,
                randomDoubleBetween(level.random, 1.2, 1.3),
                level.random.nextInt(5) + 25,
                lengthFactor = 3.5f,
                sprites = sprites
            )
        }
    }

    companion object {
        fun randomDoubleBetween(random: RandomSource, start: Double, end: Double): Double {
            val base = random.nextDouble()
            val diff = end - start
            return start + (base * diff)
        }
    }
}