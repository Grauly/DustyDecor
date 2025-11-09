package grauly.dustydecor.geometry

import grauly.dustydecor.particle.spark.QuadBasedParticleSubmittable
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf

interface ShapeDefinition {
    fun getPoints(): List<Vec3d>
    fun getUvs(): List<Vec2f>
    fun getTransformed(posOffset: Vec3d = Vec3d.ZERO, scale: Vec3d = Vec3d(1.0, 1.0, 1.0), rotation: Quaternionf = Quaternionf()): MutatedShape {
        return MutatedShape(
            getPoints().map { transformPoint(it, posOffset, scale, rotation) },
            getUvs()
        )
    }
    fun apply(vertexConsumer: VertexConsumer, minUv: Vec2f, maxUv: Vec2f, extraSetup:(VertexConsumer) -> Unit = {}) {
        val uvDiff = maxUv.add(minUv.negate())
        for (i in 0..<getPoints().size) {
            val uv = getUvs()[i]
            val uvMul = Vec2f(uv.x * uvDiff.x, uv.y * uvDiff.y)
            val vertex = vertexConsumer.vertex(getPoints()[i].toVector3f()).texture(minUv.x + uv.x * uvMul.x, minUv.y + uv.y * uvMul.y)
            extraSetup.invoke(vertex)
        }
    }
    fun applyMatrix(matrixStack: MatrixStack.Entry, vertexConsumer: VertexConsumer, minUv: Vec2f, maxUv: Vec2f, extraSetup:(VertexConsumer) -> Unit = {}) {
        val uvDiff = maxUv.add(minUv.negate())
        for (i in 0..<getPoints().size) {
            val uv = getUvs()[i]
            val uvMul = Vec2f(uv.x * uvDiff.x, uv.y * uvDiff.y)
            val vertex = vertexConsumer.vertex(matrixStack, getPoints()[i].toVector3f()).texture(minUv.x + uv.x * uvMul.x, minUv.y + uv.y * uvMul.y)
            extraSetup.invoke(vertex)
        }

    }
    fun transformPoint(point: Vec3d, posOffset: Vec3d, scale: Vec3d, rotation: Quaternionf): Vec3d {
        return Vec3d(point.toVector3f().mul(scale.x.toFloat(), scale.y.toFloat(), scale.z.toFloat()).rotate(rotation).add(posOffset.toVector3f()))
    }
    fun submitUniformly(submittable: QuadBasedParticleSubmittable, packedLight: Int, color: Int) {
        if (getPoints().size % 4 != 0) throw IllegalStateException("Non Quad Shape cannot be submitted to QuadBasedParticleSubmittable")
        var counter = 0
        for (i in 0..<getPoints().size) {
            if (counter == 0) {
                submittable.beginQuad()
            }
            val uv = getUvs()[i]
            submittable.addVertex(getPoints()[i].toVector3f(), uv.x, uv.y, packedLight, color)
            counter++
            if (counter == 4) {
                submittable.endQuad()
                counter = 0
            }
        }
    }
}