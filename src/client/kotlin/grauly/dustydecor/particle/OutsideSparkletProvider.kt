package grauly.dustydecor.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource

class OutsideSparkletProvider(private val sprites: SpriteSet): ParticleProvider<SimpleParticleType> {
    override fun createParticle(
        options: SimpleParticleType,
        level: ClientLevel,
        x: Double,
        y: Double,
        z: Double,
        xAux: Double,
        yAux: Double,
        zAux: Double,
        random: RandomSource
    ): Particle {
        val particle = VelocityPointingParticle(
            level,
            x, y, z,
            xAux, yAux, zAux,
            sprites
        )
        particle.glowing = true
        return particle
    }
}