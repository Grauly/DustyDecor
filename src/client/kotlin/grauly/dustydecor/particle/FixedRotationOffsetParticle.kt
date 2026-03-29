package grauly.dustydecor.particle

import com.mojang.blaze3d.pipeline.RenderPipeline
import grauly.dustydecor.DustyDecorMod
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.SimpleAnimatedParticle
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
    sprites: SpriteSet,
    gravity: Float
) : SimpleAnimatedParticle(level, x, y, z, sprites, gravity) {

    override fun getLayer(): Layer = RENDER_LAYER

    abstract fun getOffset(): Vector3f
    abstract fun getRotation(): Quaternionf

    override fun extract(
        renderState: QuadParticleRenderState,
        camera: Camera,
        tickProgress: Float
    ) {
        extractRotatedQuad(renderState, camera, getRotation(), tickProgress)
    }

    override fun extractRotatedQuad(
        renderState: QuadParticleRenderState,
        camera: Camera,
        rotation: Quaternionf,
        tickProgress: Float
    ) {
        val offset = Vector3f(getOffset()).rotate(rotation)
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