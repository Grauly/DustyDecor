package grauly.dustydecor.particle

import net.minecraft.client.particle.*
import net.minecraft.client.texture.Sprite
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.math.random.Random

class LightFlashParticle(
    clientWorld: ClientWorld?,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    sprite: Sprite
) : BillboardParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, sprite) {
    init {
        this.velocityX = velocityX
        this.velocityY = velocityY
        this.velocityZ = velocityZ
        this.gravityStrength = 0f
        maxAge = 4
        scale = 15/16f/2f
    }

    override fun getRenderType(): RenderType = RenderType.field_62640

    class Factory(private val spriteProvider: SpriteProvider) : ParticleFactory<SimpleParticleType> {
        override fun createParticle(
            parameters: SimpleParticleType?,
            world: ClientWorld?,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: Random
        ): Particle {
            return LightFlashParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getSprite(random))
        }
    }
}