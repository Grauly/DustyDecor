package grauly.dustydecor.particle

import com.mojang.blaze3d.pipeline.RenderPipeline
import grauly.dustydecor.DustyDecorMod
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.particle.AnimatedParticle
import net.minecraft.client.particle.BillboardParticleSubmittable
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.render.Camera
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

class AirflowParticle(
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    private val flowDirection: Direction,
    spriteProvider: SpriteProvider,
    upwardsAcceleration: Float
) : AnimatedParticle(world, x, y, z, spriteProvider, upwardsAcceleration) {
    private val axisRotationRadians: Float = world.random.nextFloat() * 2 * PI.toFloat()

    init {
        maxAge = 11
    }

    override fun getRenderType(): RenderType = RENDER_TYPE

    override fun render(
        submittable: BillboardParticleSubmittable,
        camera: Camera,
        tickProgress: Float
    ) {
        val quat = Quaternionf()
            .rotateTo(Direction.UP.floatVector, flowDirection.opposite.floatVector)
            .rotateY(axisRotationRadians)
        render(submittable, camera, quat, tickProgress)
    }

    override fun render(
        submittable: BillboardParticleSubmittable,
        camera: Camera,
        rotation: Quaternionf,
        tickProgress: Float
    ) {
        val offset = Vector3f(4/16f, -3/16f, 0f).rotate(rotation)
        val cameraPos = camera.pos
        val x1: Float = (x + offset.x - cameraPos.x).toFloat()
        val y1: Float = (y + offset.y - cameraPos.y).toFloat()
        val z1: Float = (z + offset.z - cameraPos.z).toFloat()
        this.renderVertex(submittable, rotation, x1, y1, z1, tickProgress)
    }

    class InflowFactory(private val spriteProvider: SpriteProvider) : ParticleFactory<AirInflowParticleEffect> {
        override fun createParticle(
            parameters: AirInflowParticleEffect,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: Random
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

    class OutflowFactory(private val spriteProvider: SpriteProvider) : ParticleFactory<AirOutflowParticleEffect> {
        override fun createParticle(
            parameters: AirOutflowParticleEffect,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: Random?
        ): Particle {
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
        val RENDER_TYPE = RenderType(
            false,
            SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE,
            RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
                .withCull(false)
                .withLocation(Identifier.of(DustyDecorMod.MODID, "opaque_no_cull"))
                .build()
        )
    }
}