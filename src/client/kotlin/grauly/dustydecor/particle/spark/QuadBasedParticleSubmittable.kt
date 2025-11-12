package grauly.dustydecor.particle.spark

import grauly.dustydecor.particle.QuadDataCache
import net.minecraft.client.particle.BillboardParticle
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.Submittable
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.state.CameraRenderState
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3f

class QuadBasedParticleSubmittable(
    initialBufferSize: Int = 128
) : Submittable {

    private var dataCache = QuadDataCache(initialBufferSize)

    override fun submit(
        queue: OrderedRenderCommandQueue,
        cameraRenderState: CameraRenderState
    ) {
        if (dataCache.getWrittenQuads() <= 0) return
        val matrices = MatrixStack()
        queue.submitCustom(
            matrices,
            RENDER_LAYER
        ) { matrixEnty, vertexConsumer ->
            dataCache.forEachVertex { pos, u, v, light, color ->
                vertexConsumer.vertex(pos).texture(u, v).light(light).color(color)
            }
        }
    }

    override fun onFrameEnd() {
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
        private val RENDER_LAYER = RenderLayer.of(
            "dustydecor_spark_particle",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT.vertexSize,
            false,
            false,
            BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE.pipeline,
            RenderLayer.MultiPhaseParameters.builder()
                .texture(
                    RenderPhase.Texture(
                        BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE.textureAtlasLocation,
                        false
                    )
                )
                .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                .build(false)
        )
    }
}