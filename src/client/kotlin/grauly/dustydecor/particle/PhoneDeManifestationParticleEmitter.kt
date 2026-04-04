package grauly.dustydecor.particle

import grauly.dustydecor.ModParticleTypes
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.NoRenderParticle
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource

class PhoneDeManifestationParticleEmitter(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xa: Double,
    ya: Double,
    za: Double
) : NoRenderParticle(level, x, y, z, xa, ya, za) {
    val yOffset: Float = 1/4f
    override fun tick() {
        level.addParticle(
            ModParticleTypes.OUTSIDE_BEAM_FLASH,
            x, y, z,
            0.0, 1.0, 0.0
        )
        level.addParticle(
            ModParticleTypes.OUTSIDE_BEAM_FLASH,
            x, y, z,
            0.0, -1.0, 0.0
        )
        level.addParticle(
            ModParticleTypes.OUTSIDE_SHOCKWAVE,
            x, y, z,
            0.0, 1.0, 0.0
        )
        for (i in 0..25) {
            val x1 = (random.nextFloat() - 0.5f) * 2f * yOffset
            val z1 = (random.nextFloat() - 0.5f) * 2f * yOffset
            level.addParticle(
                ModParticleTypes.OUTSIDE_SPARKLET,
                x + x1, y - yOffset, z + z1,
                0.0, 0.04, 0.0
            )
            level.addParticle(
                ModParticleTypes.OUTSIDE_SPARKLET,
                x + x1, y + yOffset, z + z1,
                0.0, -0.04, 0.0
            )
            level.addParticle(
                ModParticleTypes.OUTSIDE_SPARK,
                x + x1,
                y + yOffset,
                z + z1,
                x1.toDouble() * .8,
                yOffset.toDouble() * if (i % 2 == 0) 1.0 else -1.0 * .8,
                z1.toDouble() * .8
            )
        }
        this.remove()
    }

    class Provider(private val sprites: SpriteSet): ParticleProvider<SimpleParticleType> {
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
            return PhoneDeManifestationParticleEmitter(
                level,
                x, y, z,
                xAux, yAux, zAux,
            )
        }

    }
}