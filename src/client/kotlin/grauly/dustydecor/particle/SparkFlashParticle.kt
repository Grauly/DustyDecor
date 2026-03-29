package grauly.dustydecor.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SimpleAnimatedParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource

class SparkFlashParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    sprites: SpriteSet
) : SimpleAnimatedParticle(level, x, y, z, sprites, 0.0125f) {

    init {
        this.lifetime = 4
        this.quadSize = 3f / 16f * 0.5f

        this.xd = velocityX
        this.yd = velocityY
        this.zd = velocityZ
    }

    override fun getLayer(): Layer = Layer.OPAQUE

    class Provider(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
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
            return SparkFlashParticle(
                level,
                x, y, z, velocityX, velocityY, velocityZ, sprites
            )
        }
    }
}