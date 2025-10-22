package grauly.dustydecor.particle.spark

import com.mojang.blaze3d.systems.RenderPass
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode
import net.minecraft.client.particle.BillboardParticle
import net.minecraft.client.particle.BillboardParticleSubmittable
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.Camera
import net.minecraft.client.render.Submittable
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.command.LayeredCustomCommandRenderer
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.state.CameraRenderState
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.util.BufferAllocator
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.math.max

class SparkParticleSubmittable(
    initialBufferSize: Int = 128
) : Submittable, OrderedRenderCommandQueue.LayeredCustom {

    private var dataCache = SparkDataCache(initialBufferSize)

    //OrderedRenderCommandQueue
    override fun submit(cache: LayeredCustomCommandRenderer.VerticesCache): BillboardParticleSubmittable.Buffers? {
        BufferAllocator.fixedSized(dataCache.getSize() * 4 * VertexFormats.POSITION_COLOR_TEXTURE_LIGHT.vertexSize)
            .use { allocator ->
                val bufferBuilder =
                    BufferBuilder(allocator, DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT)
                val returnMap = mutableMapOf<BillboardParticle.RenderType, BillboardParticleSubmittable.Layer>()
                val targetLayer = BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE
                for (particle in 0..<dataCache.getSize()) {
                    val color = dataCache.readColor(particle)
                    val light = dataCache.readLight(particle)
                    for (n in 0..3) {
                        val uv = dataCache.readUv(particle, n)
                        bufferBuilder.vertex(dataCache.readPosition(particle, n))
                            .color(color)
                            .light(light)
                            .texture(uv.x, uv.y)
                    }
                }
                returnMap[targetLayer] = BillboardParticleSubmittable.Layer(0, dataCache.getSize() * 4)
                val builtBuffer = bufferBuilder.endNullable()
                if (builtBuffer == null) return null
                cache.write(builtBuffer.buffer)
                RenderSystem.getSequentialBuffer(DrawMode.QUADS)
                    .getIndexBuffer(builtBuffer.drawParameters.indexCount())
                val gpuBufferSlice = RenderSystem.getDynamicUniforms().write(
                    RenderSystem.getModelViewMatrix(),
                    Vector4f(1.0f, 1.0f, 1.0f, 1.0f),
                    Vector3f(),
                    RenderSystem.getTextureMatrix(),
                    RenderSystem.getShaderLineWidth()
                )
                return BillboardParticleSubmittable.Buffers(
                    builtBuffer.drawParameters.indexCount,
                    gpuBufferSlice,
                    returnMap
                )
            }
    }

    //Submittable
    override fun submit(
        queue: OrderedRenderCommandQueue,
        cameraRenderState: CameraRenderState
    ) {
        if (dataCache.getSize() > 0) {
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

    fun addSpark(
        from: Vec3d,
        to: Vec3d,
        camera: Camera,
        lengthFactor: Float,
        size: Float,
        sprite: Sprite,
        color: Int,
        light: Int
    ) {
        val centerPos = from.lerp(to, 0.5)
        val spanVector = from.subtract(to)
        val speedSquared = spanVector.lengthSquared()
        val sparkLength = max(lengthFactor * speedSquared.toFloat(), size)
        val rotation = if (speedSquared != 0.0) {
            Quaternionf().rotationTo(Vector3f(1f, 0f, 0f), spanVector.normalize().toVector3f())
        } else {
            Quaternionf()
        }

        val camForward = centerPos.subtract(camera.pos)
        val camRelativePos = centerPos.subtract(camera.pos)
        val localUp = Vector3f(1f, 0f, 0f).cross(camForward.toVector3f()).normalize().mul(0.5f / 16f)
        val forward = Vector3f(1f, 0f, 0f).rotate(rotation).mul(sparkLength)

        dataCache.beginWrite()

        //first vertex
        dataCache.addPosition(camRelativePos.toVector3f().add(forward).add(localUp.negate()))
        dataCache.addUv(sprite.maxU, sprite.maxV)

        //second vertex, localUp now points up again
        dataCache.addPosition(camRelativePos.toVector3f().add(forward).add(localUp.negate()))
        dataCache.addUv(sprite.maxU, sprite.minV)

        //third vertex
        dataCache.addPosition(camRelativePos.toVector3f().add(localUp))
        dataCache.addUv(sprite.minU, sprite.minV)

        //fourth vertex
        dataCache.addPosition(camRelativePos.toVector3f().add(localUp.negate()))
        dataCache.addUv(sprite.maxU, sprite.minV)

        dataCache.addColorLight(color, light)
        dataCache.finishWrite()
    }

    private class SparkDataCache(
        initialBufferSize: Int = 128
    ) {
        private var building = false
        private var particleIndex = 0
        private var capacity = initialBufferSize

        private var positionBuffer: FloatArray =
            FloatArray(capacity * POSITION_ENTRIES_PER_ENTRY * POSITIONS_PER_PARTICLE)
        private var uvBuffer: FloatArray = FloatArray(capacity * UV_ENTRIES_PER_ENTRY * UVS_PER_PARTICLE)
        private var colorLightBuffer: IntArray =
            IntArray(capacity * COLOR_LIGHT_ENTRIES_PER_COLOR_LIGHT_ENTRY * COLOR_LIGHT_ENTRIES_PER_PARTICLE)

        private var builtPositions = 0
        private var builtUvs = 0
        private var builtColorLights = 0

        fun beginWrite() {
            if (particleIndex + 1 >= capacity) {
                increaseCapacity()
            }
            building = true
        }

        fun finishWrite() {
            if (builtPositions != POSITIONS_PER_PARTICLE) throw IllegalStateException("Could not finish spark data, only $builtPositions/$POSITIONS_PER_PARTICLE positions where added")
            if (builtUvs != UVS_PER_PARTICLE) throw IllegalStateException("Could not finish spark data, only $builtUvs/$UVS_PER_PARTICLE uv's where added")
            if (builtColorLights != COLOR_LIGHT_ENTRIES_PER_PARTICLE) throw IllegalStateException("Could not finish spark data, only $builtColorLights/$COLOR_LIGHT_ENTRIES_PER_PARTICLE color/light pairs where added")
            builtPositions = 0
            builtUvs = 0
            builtColorLights = 0
            particleIndex++

            building = false
        }

        fun clear() {
            builtPositions = 0
            builtUvs = 0
            builtColorLights = 0

            particleIndex = 0
            building = false
        }

        fun getSize(): Int {
            return particleIndex + 1
        }

        private fun increaseCapacity() {
            capacity *= 2
            positionBuffer = positionBuffer.copyOf(capacity * POSITION_ENTRIES_PER_ENTRY * POSITIONS_PER_PARTICLE)
            uvBuffer = uvBuffer.copyOf(capacity * UV_ENTRIES_PER_ENTRY * UVS_PER_PARTICLE)
            colorLightBuffer =
                colorLightBuffer.copyOf(capacity * COLOR_LIGHT_ENTRIES_PER_COLOR_LIGHT_ENTRY * COLOR_LIGHT_ENTRIES_PER_PARTICLE)
        }

        fun readPosition(particle: Int, entry: Int): Vector3f {
            if (particle > particleIndex) throw IndexOutOfBoundsException("Attempting to access particle $particle, but only $particleIndex are built")
            val actualIndex =
                particle * POSITION_ENTRIES_PER_ENTRY * POSITIONS_PER_PARTICLE + entry * POSITION_ENTRIES_PER_ENTRY
            val x = positionBuffer[actualIndex + 0]
            val y = positionBuffer[actualIndex + 1]
            val z = positionBuffer[actualIndex + 2]
            return Vector3f(x, y, z)
        }

        fun addPosition(pos: Vector3f) {
            if (!building) throw IllegalStateException("Data buffer not building")
            if (builtPositions >= POSITIONS_PER_PARTICLE) throw IllegalStateException("Attempting to add position to already filled buffer")
            val positionIndex =
                particleIndex * POSITION_ENTRIES_PER_ENTRY * POSITIONS_PER_PARTICLE + builtPositions * POSITION_ENTRIES_PER_ENTRY
            positionBuffer[positionIndex + 0] = pos.x
            positionBuffer[positionIndex + 1] = pos.y
            positionBuffer[positionIndex + 2] = pos.z
            builtPositions++
        }

        fun readUv(particle: Int, entry: Int): Vec2f {
            if (particle > particleIndex) throw IndexOutOfBoundsException("Attempting to access particle $particle, but only $particleIndex are available")
            val actualIndex = particle * UV_ENTRIES_PER_ENTRY * UVS_PER_PARTICLE + entry * UV_ENTRIES_PER_ENTRY
            val u = uvBuffer[actualIndex + 0]
            val v = uvBuffer[actualIndex + 1]
            return Vec2f(u, v)
        }

        fun addUv(u: Float, v: Float) {
            if (!building) throw IllegalStateException("Data buffer not building")
            if (builtUvs >= UVS_PER_PARTICLE) throw IllegalStateException("Attempting to add uv to already filled buffer")

            val actualIndex = particleIndex * UV_ENTRIES_PER_ENTRY * UVS_PER_PARTICLE + builtUvs * UV_ENTRIES_PER_ENTRY
            uvBuffer[actualIndex + 0] = u
            uvBuffer[actualIndex + 1] = v
            builtUvs++
        }

        fun addColorLight(color: Int, light: Int) {
            if (!building) throw IllegalStateException("Data buffer not building")
            if (builtColorLights >= COLOR_LIGHT_ENTRIES_PER_PARTICLE) throw IllegalStateException("Attempting to add color/light pair to already filled buffer")

            val actualIndex =
                particleIndex * COLOR_LIGHT_ENTRIES_PER_COLOR_LIGHT_ENTRY * COLOR_LIGHT_ENTRIES_PER_PARTICLE
            colorLightBuffer[actualIndex + 0] = color
            colorLightBuffer[actualIndex + 1] = light
            builtColorLights++
        }

        fun readColor(particle: Int): Int {
            if (particle > particleIndex) throw IndexOutOfBoundsException("Attempting to access particle $particle, but only $particleIndex are available")
            val actualIndex =
                particleIndex * COLOR_LIGHT_ENTRIES_PER_COLOR_LIGHT_ENTRY * COLOR_LIGHT_ENTRIES_PER_PARTICLE
            return colorLightBuffer[actualIndex + 0]
        }

        fun readLight(particle: Int): Int {
            if (particle > particleIndex) throw IndexOutOfBoundsException("Attempting to access particle $particle, but only $particleIndex are available")
            val actualIndex =
                particleIndex * COLOR_LIGHT_ENTRIES_PER_COLOR_LIGHT_ENTRY * COLOR_LIGHT_ENTRIES_PER_PARTICLE
            return colorLightBuffer[actualIndex + 1]
        }

        companion object {
            private const val POSITIONS_PER_PARTICLE = 4
            private const val POSITION_ENTRIES_PER_ENTRY = 3

            private const val UVS_PER_PARTICLE = 4
            private const val UV_ENTRIES_PER_ENTRY = 2

            private const val COLOR_LIGHT_ENTRIES_PER_PARTICLE = 1
            private const val COLOR_LIGHT_ENTRIES_PER_COLOR_LIGHT_ENTRY = 2
        }
    }

}