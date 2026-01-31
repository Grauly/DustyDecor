package grauly.dustydecor.geometry

import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

object PlaneCrossShape : ShapeDefinition {
    override fun getPoints(): List<Vec3> = POINTS

    override fun getUvs(): List<Vec2> = UVS

    private val POINTS: List<Vec3> = run {
        val points: MutableList<Vec3> = BiPlaneShape
            .getPoints()
            .toMutableList()
        points.addAll(
            BiPlaneShape.getTransformed(Vec3.ZERO, Vec3(1.0, 1.0, 1.0), Quaternionf().fromAxisAngleRad(Vector3f(1f, 0f, 0f), (PI/2).toFloat())).getPoints()
        )
        points
    }
    private val UVS: List<Vec2> = run {
        val uvs: MutableList<Vec2> = BiPlaneShape.getUvs().toMutableList()
        uvs.addAll(BiPlaneShape.getUvs())
        uvs
    }
}