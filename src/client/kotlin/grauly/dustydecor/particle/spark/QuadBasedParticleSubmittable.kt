package grauly.dustydecor.particle.spark

import com.mojang.blaze3d.systems.RenderPass
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode
import grauly.dustydecor.particle.QuadDataCache
import net.minecraft.client.particle.BillboardParticle
import net.minecraft.client.particle.BillboardParticleSubmittable
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.Submittable
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.command.LayeredCustomCommandRenderer
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.state.CameraRenderState
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.util.BufferAllocator
import org.joml.Vector3f
import org.joml.Vector4f

class QuadBasedParticleSubmittable(
    initialBufferSize: Int = 128
) : Submittable, OrderedRenderCommandQueue.LayeredCustom {

    private var dataCache = QuadDataCache(initialBufferSize)

    //OrderedRenderCommandQueue
    override fun submit(cache: LayeredCustomCommandRenderer.VerticesCache): BillboardParticleSubmittable.Buffers? {
        BufferAllocator.fixedSized(dataCache.getWrittenQuads() * 4 * VertexFormats.POSITION_COLOR_TEXTURE_LIGHT.vertexSize)
            .use { bufferAllocator ->
                val bufferBuilder =
                    BufferBuilder(bufferAllocator, DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT)
                dataCache.forEachVertex { pos, u, v, light, color ->
                    bufferBuilder
                        .vertex(pos)
                        .texture(u, v)
                        .color(color)
                        .light(light)
                }
                val returnMap: Map<BillboardParticle.RenderType, BillboardParticleSubmittable.Layer> = mapOf(
                    (BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE to BillboardParticleSubmittable.Layer(
                        0,
                        dataCache.getWrittenQuads() * 6
                    ))
                )
                val builtBuffer = bufferBuilder.endNullable() ?: return null
                cache.write(builtBuffer.buffer)
                RenderSystem.getSequentialBuffer(DrawMode.QUADS).getIndexBuffer(builtBuffer.drawParameters.indexCount())
                val gpuBufferSlice = RenderSystem.getDynamicUniforms()
                    .write(
                        RenderSystem.getModelViewMatrix(),
                        Vector4f(1f, 1f, 1f, 1f),
                        Vector3f(),
                        RenderSystem.getTextureMatrix(),
                        RenderSystem.getShaderLineWidth()
                    )
                return BillboardParticleSubmittable.Buffers(builtBuffer.drawParameters.indexCount(), gpuBufferSlice, returnMap)
            }
    }

    //Submittable
    override fun submit(
        queue: OrderedRenderCommandQueue,
        cameraRenderState: CameraRenderState
    ) {
        if (dataCache.getWrittenQuads() > 0) {
            queue.submitCustom(this)
        }
    }

    //OrderedRenderCommandQueue
    override fun render(
        buffers: BillboardParticleSubmittable.Buffers,
        cache: LayeredCustomCommandRenderer.VerticesCache,
        renderPass: RenderPass,
        manager: TextureManager,
        translucent: Boolean
    ) {
        val shapeIndexBuffer = RenderSystem.getSequentialBuffer(DrawMode.QUADS)
        renderPass.setVertexBuffer(0, cache.get())
        renderPass.setIndexBuffer(
            shapeIndexBuffer.getIndexBuffer(buffers.indexCount),
            shapeIndexBuffer.indexType
        )
        renderPass.setUniform("DynamicTransforms", buffers.dynamicTransforms)

        for (entry in buffers.layers.entries) {
            if (translucent == (entry.key as BillboardParticle.RenderType).translucent()) {
                renderPass.setPipeline((entry.key as BillboardParticle.RenderType).pipeline())
                renderPass.bindSampler(
                    "Sampler0",
                    manager
                        .getTexture((entry.key as BillboardParticle.RenderType).textureAtlasLocation())
                        .getGlTextureView()
                )
                renderPass.drawIndexed(
                    (entry.value as BillboardParticleSubmittable.Layer).vertexOffset,
                    0,
                    (entry.value as BillboardParticleSubmittable.Layer).indexCount,
                    1
                )
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
            u,v,
            light, color
        )
    }
}