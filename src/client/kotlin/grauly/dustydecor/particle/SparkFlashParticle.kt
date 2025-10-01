package grauly.dustydecor.particle

import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.math.random.Random

class SparkFlashParticle(
    clientWorld: ClientWorld?,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    private val spriteProvider: SpriteProvider
) : AnimatedParticle(clientWorld, x, y, z, spriteProvider, 0.0125f) {

    init {
        this.maxAge = 4
        this.scale = 3f/16f * 0.5f

        this.velocityX = velocityX
        this.velocityY = velocityY
        this.velocityZ = velocityZ
    }

    override fun getRenderType(): RenderType = RenderType.PARTICLE_ATLAS_OPAQUE

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
            return SparkFlashParticle(
                world,
                x, y, z, velocityX, velocityY, velocityZ, spriteProvider
            )
        }
    }
}