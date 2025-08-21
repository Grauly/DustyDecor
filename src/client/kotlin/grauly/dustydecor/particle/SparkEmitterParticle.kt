package grauly.dustydecor.particle

import grauly.dustydecor.ModParticleTypes
import net.minecraft.client.particle.NoRenderParticle
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.world.ClientWorld

class SparkEmitterParticle(
    clientWorld: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    private val xDir: Double,
    private val yDir: Double,
    private val zDir: Double,
    private val spread: Double,
    private val amount: Int
) : NoRenderParticle(clientWorld, x, y, z) {

    override fun tick() {
        for (i in 1..amount) {
            val xOffset = (random.nextDouble() - 0.5) * spread
            val yOffset = (random.nextDouble() - 0.5) * spread
            val zOffset = (random.nextDouble() - 0.5) * spread
            world.addParticleClient(
                if (random.nextDouble() > 0.4) ModParticleTypes.SPARK_PARTICLE_TYPE else ModParticleTypes.SMALL_SPARK_PARTICLE_TYPE,
                x, y, z, xDir + xOffset, yDir + yOffset, zDir + zOffset
            )
        }
        this.markDead()
    }

    class Factory(spriteProvider: SpriteProvider) : ParticleFactory<SparkEmitterParticleEffect> {
        override fun createParticle(
            parameters: SparkEmitterParticleEffect,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ): Particle {
            return SparkEmitterParticle(
                world,
                x,
                y,
                z,
                velocityX,
                velocityY,
                velocityZ,
                parameters.spread,
                parameters.amount
            )
        }
    }
}