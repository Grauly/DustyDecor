package grauly.dustydecor.particle

import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.state.level.QuadParticleRenderState
import net.minecraft.core.Direction
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.sign

open class VelocityPointingParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xa: Double,
    ya: Double,
    za: Double,
    sprites: SpriteSet,
) : FixedRotationOffsetParticle(level, x, y, z, xa, ya, za, sprites) {
    init {
        this.xd = xa
        this.yd = ya
        this.zd = za
        friction = .9f
    }

    override fun tick() {
        super.tick()
        setSpriteFromAge(sprites)
    }

    override fun getOffset(camera: Camera, tickProgress: Float): Vector3f {
        return Vector3f(0.0F, 0.0F, 0.0F)
    }

    override fun getRotation(camera: Camera, tickProgress: Float): Quaternionf {
        val rotation = Quaternionf()
        rotation.rotationTo(Direction.UP.unitVec3f, Vec3(xd, yd, zd).toVector3f())
        val cameraRotation = Quaternionf()
        facingCameraMode.setRotation(cameraRotation, camera, tickProgress)
        if (sign(yd) < 0) cameraRotation.conjugate()
        return rotation.mul(cameraRotation)
    }

    override fun getFacingCameraMode(): FacingCameraMode = FacingCameraMode.LOOKAT_Y

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
            return VelocityPointingParticle(
                level,
                x, y, z,
                xAux, yAux, zAux,
                sprites
            )
        }
    }
}