package grauly.dustydecor.particle

import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.world.ClientWorld

class SparkEmitterFactory(spriteProvider: SpriteProvider) : ParticleFactory<SparkEmitterParticleEffect> {
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
        val constructor = if (parameters.block) ::BlockSparkEmitterParticle else ::SparkEmitterParticle
        return constructor.invoke(world, x, y, z, velocityX, velocityY, velocityZ, parameters.spread, parameters.amount)
    }

}