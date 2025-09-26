package grauly.dustydecor.particle.spark

import net.minecraft.client.render.Camera
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.Submittable
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.state.CameraRenderState
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.max

class SparkParticleSubmittable(
    initialBufferSize: Int = 128
) : Submittable {

    private var buffer = SparkDataBuffer(initialBufferSize)

    override fun submit(
        queue: OrderedRenderCommandQueue,
        cameraRenderState: CameraRenderState
    ) {
        val matrixStack = MatrixStack()
        for (particle in 0..<buffer.getSize()) {
            matrixStack.push()

            matrixStack.translate(Vec3d(buffer.readPosition(particle, 0)))

            queue.submitCustom(
                matrixStack,
                RenderLayer.getEntityCutout(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE)
            )
            { matrixEntry, vertexConsumer ->
                val normal = buffer.readPosition(particle, 1)
                for (n in 0..3) {
                    vertexConsumer.vertex(matrixEntry, buffer.readPosition(particle, n + 2))
                        .color(buffer.readColor(particle)).light(buffer.readLight(particle))
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .texture(buffer.readUv(particle, n).x, buffer.readUv(particle, n).y)
                        .normal(matrixEntry, normal)
                }
            }

            matrixStack.pop()
        }
    }

    override fun onFrameEnd() {
        buffer.clear()
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
        val localUp = Vector3f(1f, 0f, 0f).cross(camForward.toVector3f()).normalize().mul(0.5f / 16f)
        val forward = Vector3f(1f, 0f, 0f).rotate(rotation).mul(sparkLength)

        buffer.beginWrite()
        buffer.addPosition(camForward.toVector3f()) //cam offset
        buffer.addPosition(camForward.negate().normalize().toVector3f()) //the normal

        buffer.addPosition(Vector3f(forward).add(localUp.negate())) //first vertex
        buffer.addUv(sprite.maxU, sprite.maxV)

        buffer.addPosition(Vector3f(forward).add(localUp.negate())) //second vertex, localUp now points up again
        buffer.addUv(sprite.maxU, sprite.minV)

        buffer.addPosition(localUp) //third vertex
        buffer.addUv(sprite.minU, sprite.minV)

        buffer.addPosition(localUp.negate()) //fourth vertex
        buffer.addUv(sprite.maxU, sprite.minV)

        buffer.addColorLight(color, light)
        buffer.finishWrite()
    }

    private class SparkDataBuffer(
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
            private const val POSITIONS_PER_PARTICLE = 6
            private const val POSITION_ENTRIES_PER_ENTRY = 3

            private const val UVS_PER_PARTICLE = 4
            private const val UV_ENTRIES_PER_ENTRY = 2

            private const val COLOR_LIGHT_ENTRIES_PER_PARTICLE = 1
            private const val COLOR_LIGHT_ENTRIES_PER_COLOR_LIGHT_ENTRY = 2
        }
    }

}