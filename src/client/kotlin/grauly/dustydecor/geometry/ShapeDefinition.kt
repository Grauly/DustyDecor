package grauly.dustydecor.geometry

import net.minecraft.client.render.VertexConsumer
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Vector4f

interface ShapeDefinition {
    fun getPoints(): List<Vec3d>
    fun getUvs(): List<Vec2f>
    fun getTransformed(posOffset: Vec3d, scale: Vec3d, axisRotations: Vec3d): MutatedShape {
        return MutatedShape(
            getPoints().map { transformPoint(it, posOffset, scale, axisRotations) },
            getUvs()
        )
    }
    fun apply(vertexConsumer: VertexConsumer, light: Int, color: Int) {
        for (i in 0..getPoints().size) {
            val uv = getUvs()[i]
            vertexConsumer.vertex(getPoints()[i].toVector3f()).texture(uv.x, uv.y).light(light).color(color)
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