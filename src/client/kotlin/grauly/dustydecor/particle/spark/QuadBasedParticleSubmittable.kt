package grauly.dustydecor.particle.spark

import grauly.dustydecor.particle.QuadDataCache
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.renderer.state.ParticleGroupRenderState
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.state.CameraRenderState
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import org.joml.Vector3f

class QuadBasedParticleSubmittable(
    initialBufferSize: Int = 128
) : ParticleGroupRenderState {

    private var dataCache = QuadDataCache(initialBufferSize)

    override fun submit(
        collector: SubmitNodeCollector,
        cameraRenderState: CameraRenderState
    ) {
        if (dataCache.getWrittenQuads() <= 0) return
        val poseStack = PoseStack()
        collector.submitCustomGeometry(
            poseStack,
            RENDER_TYPE
        ) { matrixEnty, vertexConsumer ->
            dataCache.forEachVertex { pos, u, v, light, color ->
                vertexConsumer.addVertex(pos).setUv(u, v).setLight(light).setColor(color)
            }
        }
    }

    override fun clear() {
        dataCache.clear()
    }

    fun beginQuad() {
        dataCache.beginQuad()
    }

    fun endQuad() {
        dataCache.endQuad()
    }

    fun addVertex(
        pos: Vector3f,
        u: Float,
        v: Float,
        light: Int,
        color: Int
    ) {
        dataCache.insertVertex(
            pos.x, pos.y, pos.z,
            u, v,
            light, color
        )
    }

    companion object {
        private val RENDER_TYPE = RenderType.create(
            "dustydecor_spark_particle",
            RenderSetup.builder(RenderPipelines.OPAQUE_PARTICLE)
                .withTexture("Sampler0", SingleQuadParticle.Layer.OPAQUE.textureAtlasLocation)
                .useLightmap()
                .createRenderSetup()
        )
    }
}