package grauly.dustydecor.particle

import com.mojang.blaze3d.pipeline.RenderPipeline
import grauly.dustydecor.DustyDecorMod
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.SimpleAnimatedParticle
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.state.level.QuadParticleRenderState
import net.minecraft.resources.Identifier
import org.joml.Quaternionf
import org.joml.Vector3f

abstract class FixedRotationOffsetParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xa: Double,
    ya: Double,
    za: Double,
    protected val sprites: SpriteSet
) : SingleQuadParticle(level, x, y, z, xa, ya, za, sprites.first()) {
    init {
        setSpriteFromAge(sprites)
    }

    override fun getLayer(): Layer = RENDER_LAYER

    abstract fun getOffset(camera: Camera, tickProgress: Float): Vector3f
    abstract fun getRotation(camera: Camera, tickProgress: Float): Quaternionf

    override fun tick() {
        setSpriteFromAge(sprites)
        super.tick()
    }

    override fun extract(
        renderState: QuadParticleRenderState,
        camera: Camera,
        tickProgress: Float
    ) {
        extractRotatedQuad(renderState, camera, getRotation(camera, tickProgress), tickProgress)
    }

    override fun extractRotatedQuad(
        renderState: QuadParticleRenderState,
        camera: Camera,
        rotation: Quaternionf,
        tickProgress: Float
    ) {
        val offset = Vector3f(getOffset(camera, tickProgress)).rotate(rotation)
        val cameraPos = camera.position()
        val x1: Float = (x + offset.x - cameraPos.x).toFloat()
        val y1: Float = (y + offset.y - cameraPos.y).toFloat()
        val z1: Float = (z + offset.z - cameraPos.z).toFloat()
        this.extractRotatedQuad(renderState, rotation, x1, y1, z1, tickProgress)
    }

    companion object {
        val RENDER_LAYER = Layer(
            false,
            Layer.OPAQUE.textureAtlasLocation(),
            RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
                .withCull(false)
                .withLocation(Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "opaque_no_cull"))
                .build()
        )
    }
}