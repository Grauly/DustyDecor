package grauly.dustydecor.particle

import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.util.RandomSource

class SparkEmitterFactory(spriteProvider: SpriteSet) : ParticleProvider<SparkEmitterParticleEffect> {
    override fun createParticle(
        parameters: SparkEmitterParticleEffect,
        world: ClientLevel,
        x: Double,
        y: Double,
        z: Double,
        velocityX: Double,
        velocityY: Double,
        velocityZ: Double,
        random: RandomSource
    ): Particle {
        val constructor = if (parameters.block) ::BlockSparkEmitterParticle else ::SparkEmitterParticle
        return constructor.invoke(world, x, y, z, velocityX, velocityY, velocityZ, parameters.spread, parameters.amount)
    }

}