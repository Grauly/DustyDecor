package grauly.dustydecor.particle

import grauly.dustydecor.DustyDecorMod
import org.joml.Vector3f

class QuadDataCache(
    initialQuadBufferSize: Int = 128,
    private val growthAmount: Int = 64,
) {
    private var capacity = initialQuadBufferSize
    private var floatBuffer: FloatArray = FloatArray(0)
    private var intBuffer: IntArray = IntArray(0)

    private var writtenQuads = 0
    private var buildingQuad = false
    private var writtenVertices = 0;

    init {
        sizeBuffers(capacity)
    }

    private fun sizeBuffers(size: Int) {
        floatBuffer = floatBuffer.copyOf(size * VERTS_PER_QUAD * FLOAT_ENTRIES_PER_VERT)
        intBuffer = intBuffer.copyOf(size * VERTS_PER_QUAD * INT_ENTRIES_PER_VERT)
        DustyDecorMod.logger.info("Resizing QuadDataCache from $capacity to $size")
        capacity = size
    }

    fun getWrittenQuads(): Int = writtenQuads

    fun beginQuad() {
        if (writtenQuads + 1 > capacity) {
            sizeBuffers(capacity + growthAmount)
        }
        if (buildingQuad) throw IllegalStateException("Already building quad, cannot begin a new one")
        buildingQuad = true
    }

    fun endQuad() {
        if (writtenVertices != VERTS_PER_QUAD) throw IllegalStateException("Failed to build Quad: only $writtenVertices/$VERTS_PER_QUAD vertices where written")
        writtenVertices = 0
        writtenQuads++
        buildingQuad = false
    }

    fun clear() {
        writtenQuads = 0
        buildingQuad = false
        writtenVertices = 0
    }

    fun insertVertex(
        x: Float,
        y: Float,
        z: Float,
        u: Float,
        v: Float,
        packedLight: Int,
        color: Int
    ) {
        if (writtenVertices + 1 > VERTS_PER_QUAD) throw IllegalArgumentException("Attempting to write extra vertex, already wrote $writtenVertices/$VERTS_PER_QUAD")
        var writeIndex =
            writtenQuads * VERTS_PER_QUAD * FLOAT_ENTRIES_PER_VERT + writtenVertices * FLOAT_ENTRIES_PER_VERT
        floatBuffer[writeIndex++] = x
        floatBuffer[writeIndex++] = y
        floatBuffer[writeIndex++] = z
        floatBuffer[writeIndex++] = u
        floatBuffer[writeIndex] = v

        writeIndex = writtenQuads * VERTS_PER_QUAD * INT_ENTRIES_PER_VERT + writtenVertices * INT_ENTRIES_PER_VERT
        intBuffer[writeIndex++] = packedLight
        intBuffer[writeIndex] = color

        writtenVertices++
    }

    fun forEachVertex(vertexAction: VertexConsumer) {
        if (buildingQuad) throw IllegalStateException("Cannot Iterate Vertices, Quad guarantee not given, cache is still building")
        var floatReadIndex = 0
        var intReadIndex = 0
        for (i in 0..<(writtenQuads * VERTS_PER_QUAD)) {
            vertexAction.consume(
                Vector3f(
                    floatBuffer[floatReadIndex++],
                    floatBuffer[floatReadIndex++],
                    floatBuffer[floatReadIndex++],
                ),
                floatBuffer[floatReadIndex++],
                floatBuffer[floatReadIndex++],
                intBuffer[intReadIndex++],
                intBuffer[intReadIndex++],
            )
        }
    }

    fun interface VertexConsumer {
        fun consume(pos: Vector3f, u: Float, v: Float, light: Int, color: Int)
    }

    private companion object {
        const val VERTS_PER_QUAD = 4
        const val FLOAT_ENTRIES_PER_VERT = 5
        const val INT_ENTRIES_PER_VERT = 2
    }
}