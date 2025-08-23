package grauly.dustydecor.particle

import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType

class SparkFlashParticle(
    clientWorld: ClientWorld?,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    private val spriteProvider: SpriteProvider
) :
    SpriteBillboardParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ) {

    init {
        setSpriteForAge(spriteProvider)
        this.maxAge = 4
        this.scale = 3f/16f * 0.5f

        this.velocityX = velocityX
        this.velocityY = velocityY
        this.velocityZ = velocityZ
    }

    override fun setSpriteForAge(spriteProvider: SpriteProvider) {
        setSprite(spriteProvider.getSprite(age+1, maxAge))
    }

    override fun tick() {
        setSpriteForAge(spriteProvider)
        super.tick()
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
            return SparkFlashParticle(
                world,
                x, y, z, velocityX, velocityY, velocityZ, spriteProvider
            )
        }
    }
}