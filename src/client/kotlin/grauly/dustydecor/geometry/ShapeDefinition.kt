package grauly.dustydecor.geometry

import grauly.dustydecor.particle.spark.QuadBasedParticleSubmittable
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf

interface ShapeDefinition {
    fun getPoints(): List<Vec3>
    fun getUvs(): List<Vec2>
    fun getTransformed(posOffset: Vec3 = Vec3.ZERO, scale: Vec3 = Vec3(1.0, 1.0, 1.0), rotation: Quaternionf = Quaternionf()): MutatedShape {
        return MutatedShape(
            getPoints().map { transformPoint(it, posOffset, scale, rotation) },
            getUvs()
        )
    }
    fun apply(vertexConsumer: VertexConsumer, minUv: Vec2, maxUv: Vec2, extraSetup:(VertexConsumer) -> Unit = {}) {
        val uvDiff = maxUv.add(minUv.negated())
        for (i in 0..<getPoints().size) {
            val uv = getUvs()[i]
            val uvMul = Vec2(uv.x * uvDiff.x, uv.y * uvDiff.y)
            val vertex = vertexConsumer.addVertex(getPoints()[i].toVector3f()).setUv(minUv.x + uv.x * uvMul.x, minUv.y + uv.y * uvMul.y)
            extraSetup.invoke(vertex)
        }
    }
    fun applyMatrix(matrixStack: PoseStack.Pose, vertexConsumer: VertexConsumer, minUv: Vec2, maxUv: Vec2, extraSetup:(VertexConsumer) -> Unit = {}) {
        val uvDiff = maxUv.add(minUv.negated())
        for (i in 0..<getPoints().size) {
            val uv = getUvs()[i]
            val uvMul = Vec2(uv.x * uvDiff.x, uv.y * uvDiff.y)
            val vertex = vertexConsumer.addVertex(matrixStack, getPoints()[i].toVector3f()).setUv(minUv.x + uv.x * uvMul.x, minUv.y + uv.y * uvMul.y)
            extraSetup.invoke(vertex)
        }

    }
    fun transformPoint(point: Vec3, posOffset: Vec3, scale: Vec3, rotation: Quaternionf): Vec3 {
        return Vec3(point.toVector3f().mul(scale.x.toFloat(), scale.y.toFloat(), scale.z.toFloat()).rotate(rotation).add(posOffset.toVector3f()))
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