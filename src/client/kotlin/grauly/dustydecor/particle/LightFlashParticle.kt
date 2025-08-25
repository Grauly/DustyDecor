package grauly.dustydecor.particle

import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType

class LightFlashParticle(
    clientWorld: ClientWorld?,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    spriteProvider: SpriteProvider
) : SpriteBillboardParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ) {
    init {
        this.velocityX = velocityX
        this.velocityY = velocityY
        this.velocityZ = velocityZ
        this.gravityStrength = 0f
        setSprite(spriteProvider)
        maxAge = 4
        scale = 15/16f/2f
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_OPAQUE

    class Factory(private val spriteProvider: SpriteProvider) : ParticleFactory<SimpleParticleType> {
        override fun createParticle(
            parameters: SimpleParticleType?,
            world: ClientWorld?,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ): Particle {
            return LightFlashParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider)
        }
    }
}