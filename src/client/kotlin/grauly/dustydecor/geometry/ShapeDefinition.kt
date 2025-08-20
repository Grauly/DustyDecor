package grauly.dustydecor.geometry

import net.minecraft.client.render.VertexConsumer
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

interface ShapeDefinition {
    fun getPoints(): List<Vec3d>
    fun getUvs(): List<Vec2f>
    fun getTransformed(posOffset: Vec3d, scale: Vec3d, axisRotations: Vec3d): MutatedShape {
        return MutatedShape(
            getPoints().map { transformPoint(it, posOffset, scale, axisRotations) },
            getUvs()
        )
    }
    fun apply(vertexConsumer: VertexConsumer, light: Int, color: Int, minUv: Vec2f, maxUv: Vec2f) {
        val uvDiff = maxUv.add(minUv.negate())
        val uvMuls = listOf(
            uvDiff,
            Vec2f(uvDiff.x, 0f),
            Vec2f(0f, 0f),
            Vec2f(0f, uvDiff.y)
        )
        for (i in 0..<getPoints().size) {
            val uv = getUvs()[i]
            val uvMul = uvMuls[i % 4]
            vertexConsumer.vertex(getPoints()[i].toVector3f()).texture(minUv.x + uv.x * uvMul.x, minUv.y + uv.y * uvMul.y).light(light).color(color)
        }
    }
    fun transformPoint(point: Vec3d, posOffset: Vec3d, scale: Vec3d, axisRotations: Vec3d): Vec3d {
        return point
            .rotateX(axisRotations.x.toFloat())
            .rotateY(axisRotations.y.toFloat())
            .rotateZ(axisRotations.z.toFloat())
            .multiply(scale)
            .add(posOffset)
    }
}