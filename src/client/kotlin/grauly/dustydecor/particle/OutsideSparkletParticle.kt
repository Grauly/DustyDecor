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

class OutsideSparkletParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xa: Double,
    ya: Double,
    za: Double,
    private val sprites: SpriteSet,
) : SingleQuadParticle(level, x, y, z, xa, ya, za, sprites.first()) {
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

    override fun getLayer(): Layer = FixedRotationOffsetParticle.RENDER_LAYER

    override fun extract(particleTypeRenderState: QuadParticleRenderState, camera: Camera, partialTickTime: Float) {
        val rotation = Quaternionf()
        rotation.rotationTo(Direction.UP.unitVec3f, Vec3(xd, yd, zd).toVector3f())
        rotation.mul(Quaternionf(0f, camera.rotation().y, 0f, camera.rotation().w))
        extractRotatedQuad(particleTypeRenderState, camera, rotation, partialTickTime)
    }

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
            return OutsideSparkletParticle(
                level,
                x, y, z,
                xAux, yAux, zAux,
                sprites
            )
        }

    }
}