package grauly.dustydecor.geometry

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

object PlaneCrossShape : ShapeDefinition {
    override fun getPoints(): List<Vec3d> = POINTS

    override fun getUvs(): List<Vec2f> = UVS

    private val POINTS: List<Vec3d> = run {
        val points: MutableList<Vec3d> = BiPlaneShape
            .getPoints()
            .toMutableList()
        points.addAll(
            BiPlaneShape.getTransformed(Vec3d.ZERO, Vec3d(1.0, 1.0, 1.0), Vec3d(Math.PI / 2, 0.0, 0.0)).getPoints()
        )
        points
    }
    private val UVS: List<Vec2f> = run {
        val uvs: MutableList<Vec2f> = BiPlaneShape.getUvs().toMutableList()
        uvs.addAll(BiPlaneShape.getUvs())
        uvs
    }
}