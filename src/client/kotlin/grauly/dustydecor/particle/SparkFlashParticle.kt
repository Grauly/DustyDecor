package grauly.dustydecor.particle

import net.minecraft.client.particle.*
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource

class SparkFlashParticle(
    clientWorld: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    private val spriteProvider: SpriteSet
) : SimpleAnimatedParticle(clientWorld, x, y, z, spriteProvider, 0.0125f) {

    init {
        this.lifetime = 4
        this.quadSize = 3f/16f * 0.5f

        this.xd = velocityX
        this.yd = velocityY
        this.zd = velocityZ
    }

    override fun getLayer(): Layer = Layer.OPAQUE

    class Factory(private val spriteProvider: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            parameters: SimpleParticleType,
            world: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle? {
            return SparkFlashParticle(
                world,
                x, y, z, velocityX, velocityY, velocityZ, spriteProvider
            )
        }
    }
}