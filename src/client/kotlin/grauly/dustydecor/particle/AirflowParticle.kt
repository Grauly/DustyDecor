package grauly.dustydecor.particle

import com.mojang.blaze3d.pipeline.RenderPipeline
import grauly.dustydecor.DustyDecorMod
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.particle.SimpleAnimatedParticle
import net.minecraft.client.renderer.state.QuadParticleRenderState
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.Camera
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.Identifier
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

class AirflowParticle(
    world: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    private val flowDirection: Direction,
    spriteProvider: SpriteSet,
    upwardsAcceleration: Float
) : SimpleAnimatedParticle(world, x, y, z, spriteProvider, upwardsAcceleration) {
    private val axisRotationRadians: Float = world.random.nextFloat() * 2 * PI.toFloat()

    init {
        lifetime = 11
    }

    override fun getLayer(): Layer = RENDER_TYPE

    override fun extract(
        submittable: QuadParticleRenderState,
        camera: Camera,
        tickProgress: Float
    ) {
        val quat = Quaternionf()
            .rotateTo(Direction.UP.unitVec3f, flowDirection.opposite.unitVec3f)
            .rotateY(axisRotationRadians)
        extractRotatedQuad(submittable, camera, quat, tickProgress)
    }

    override fun extractRotatedQuad(
        submittable: QuadParticleRenderState,
        camera: Camera,
        rotation: Quaternionf,
        tickProgress: Float
    ) {
        val offset = Vector3f(4/16f, -3/16f, 0f).rotate(rotation)
        val cameraPos = camera.position()
        val x1: Float = (x + offset.x - cameraPos.x).toFloat()
        val y1: Float = (y + offset.y - cameraPos.y).toFloat()
        val z1: Float = (z + offset.z - cameraPos.z).toFloat()
        this.extractRotatedQuad(submittable, rotation, x1, y1, z1, tickProgress)
    }

    class InflowFactory(private val spriteProvider: SpriteSet) : ParticleProvider<AirInflowParticleEffect> {
        override fun createParticle(
            parameters: AirInflowParticleEffect,
            world: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle {
            return AirflowParticle(
                world,
                x, y, z,
                parameters.inflowDirection.opposite,
                spriteProvider,
                0.0f
            )
        }
    }

    class OutflowFactory(private val spriteProvider: SpriteSet) : ParticleProvider<AirOutflowParticleEffect> {
        override fun createParticle(
            parameters: AirOutflowParticleEffect,
            world: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle? {
            return AirflowParticle(
                world,
                x, y, z,
                parameters.outflowDirection,
                spriteProvider,
                0.0f
            )
        }
    }

    companion object {
        val RENDER_TYPE = Layer(
            false,
            TextureAtlas.LOCATION_PARTICLES,
            RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
                .withCull(false)
                .withLocation(Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "opaque_no_cull"))
                .build()
        )
    }
}