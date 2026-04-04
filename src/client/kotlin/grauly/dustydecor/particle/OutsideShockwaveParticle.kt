package grauly.dustydecor.particle

import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

class OutsideShockwaveParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xa: Double,
    ya: Double,
    za: Double,
    sprites: SpriteSet
) : FixedRotationOffsetParticle(level, x, y, z, xa, ya, za, sprites) {

    init {
        xd = 0.0
        yd = 0.0
        zd = 0.0
        quadSize = .8f
        lifetime = 16
    }

    val rotation: Quaternionf = Quaternionf()
        .rotateX((PI/2f).toFloat())
        .rotateZ(random.nextFloat() * (PI/2f).toFloat())

    override fun getOffset(camera: Camera, tickProgress: Float): Vector3f = Vector3f(0f, 0f, 0f)

    override fun getRotation(camera: Camera, tickProgress: Float): Quaternionf = rotation

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
            val particle = OutsideShockwaveParticle(
                level,
                x, y, z,
                xAux, yAux, zAux,
                sprites
            )
            particle.glowing = true
            return particle
        }

    }
}