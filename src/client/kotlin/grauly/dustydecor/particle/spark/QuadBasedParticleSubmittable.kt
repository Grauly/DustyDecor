package grauly.dustydecor.particle.spark

import grauly.dustydecor.particle.QuadDataCache
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.state.ParticleGroupRenderState
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.state.CameraRenderState
import com.mojang.blaze3d.vertex.PoseStack
import org.joml.Vector3f

class QuadBasedParticleSubmittable(
    initialBufferSize: Int = 128
) : ParticleGroupRenderState {

    private var dataCache = QuadDataCache(initialBufferSize)

    override fun submit(
        queue: SubmitNodeCollector,
        cameraRenderState: CameraRenderState
    ) {
        if (dataCache.getWrittenQuads() <= 0) return
        val matrices = PoseStack()
        queue.submitCustomGeometry(
            matrices,
            RENDER_LAYER
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
        private val RENDER_LAYER = RenderType.create(
            "dustydecor_spark_particle",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP.vertexSize,
            false,
            false,
            SingleQuadParticle.Layer.OPAQUE.pipeline,
            RenderType.CompositeState.builder()
                .setTextureState(
                    RenderStateShard.TextureStateShard(
                        SingleQuadParticle.Layer.OPAQUE.textureAtlasLocation,
                        false
                    )
                )
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .createCompositeState(false)
        )
    }
}