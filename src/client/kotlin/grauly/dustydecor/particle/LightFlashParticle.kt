package grauly.dustydecor.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource

class LightFlashParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    sprite: TextureAtlasSprite
) : SingleQuadParticle(level, x, y, z, velocityX, velocityY, velocityZ, sprite) {
    init {
        this.xd = velocityX
        this.yd = velocityY
        this.zd = velocityZ
        this.gravity = 0f
        lifetime = 4
        quadSize = 15 / 16f / 2f
    }

    override fun getLayer(): Layer = Layer.OPAQUE

    class Provider(private val spriteProvider: SpriteSet) : ParticleProvider<SimpleParticleType> {
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
            return LightFlashParticle(level, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.get(random))
        }
    }
}