package grauly.dustydecor.particle

import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource
import org.joml.Vector3f

class NonMovingVelocityPointingParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xa: Double,
    ya: Double,
    za: Double,
    sprites: SpriteSet
) : VelocityPointingParticle(level, x, y, z, xa, ya, za, sprites) {
    init {
        lifetime = 20
        quadSize = .5f
    }

    override fun tick() {
        if (age++ > lifetime) this.remove()
        setSpriteFromAge(sprites)
    }

    override fun getOffset(camera: Camera, tickProgress: Float): Vector3f =
        Vector3f(
            -1/32f,
            quadSize,
            0f
        )

    class Provider(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
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
            return NonMovingVelocityPointingParticle(
                level,
                x, y, z,
                xAux, yAux, zAux,
                sprites
            )
        }
    }
}