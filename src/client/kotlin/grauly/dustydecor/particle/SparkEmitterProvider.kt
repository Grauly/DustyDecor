package grauly.dustydecor.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.util.RandomSource

class SparkEmitterProvider(sprites: SpriteSet) : ParticleProvider<SparkEmitterParticleOptions> {
    override fun createParticle(
        options: SparkEmitterParticleOptions,
        level: ClientLevel,
        x: Double,
        y: Double,
        z: Double,
        velocityX: Double,
        velocityY: Double,
        velocityZ: Double,
        random: RandomSource
    ): Particle {
        val constructor = if (options.block) ::BlockSparkEmitterParticle else ::SparkEmitterParticle
        return constructor.invoke(level, x, y, z, velocityX, velocityY, velocityZ, options.spread, options.amount)
    }

}