package grauly.dustydecor.geometry

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

object PlaneCrossShape : ShapeDefinition {
    override fun getPoints(): List<Vec3d> = POINTS

    override fun getUvs(): List<Vec2f> = UVS

    private val POINTS: List<Vec3d> = run {
        val points: MutableList<Vec3d> = BiPlaneShape
            .getPoints()
            .toMutableList()
        points.addAll(
            BiPlaneShape.getTransformed(Vec3d.ZERO, Vec3d(1.0, 1.0, 1.0), Quaternionf().fromAxisAngleRad(Vector3f(1f, 0f, 0f), (PI/2).toFloat())).getPoints()
        )
        points
    }
    private val UVS: List<Vec2f> = run {
        val uvs: MutableList<Vec2f> = BiPlaneShape.getUvs().toMutableList()
        uvs.addAll(BiPlaneShape.getUvs())
        uvs
    }
}