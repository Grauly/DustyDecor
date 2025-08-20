package grauly.dustydecor.geometry

import net.minecraft.client.render.VertexConsumer
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf

interface ShapeDefinition {
    fun getPoints(): List<Vec3d>
    fun getUvs(): List<Vec2f>
    fun getTransformed(posOffset: Vec3d, scale: Vec3d, rotation: Quaternionf): MutatedShape {
        return MutatedShape(
            getPoints().map { transformPoint(it, posOffset, scale, rotation) },
            getUvs()
        )
    }
    fun apply(vertexConsumer: VertexConsumer, light: Int, color: Int, minUv: Vec2f, maxUv: Vec2f) {
        val uvDiff = maxUv.add(minUv.negate())
        for (i in 0..<getPoints().size) {
            val uv = getUvs()[i]
            val uvMul = Vec2f(uv.x * uvDiff.x, uv.y * uvDiff.y)
            vertexConsumer.vertex(getPoints()[i].toVector3f()).texture(minUv.x + uv.x * uvMul.x, minUv.y + uv.y * uvMul.y).light(light).color(color)
        }
    }
    fun transformPoint(point: Vec3d, posOffset: Vec3d, scale: Vec3d, rotation: Quaternionf): Vec3d {
        return Vec3d(point.toVector3f().mul(scale.x.toFloat(), scale.y.toFloat(), scale.z.toFloat()).rotate(rotation).add(posOffset.toVector3f()))
    }
}