package grauly.dustydecor.geometry

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

object BiPlaneShape: ShapeDefinition {
    override fun getPoints(): List<Vec3d> = run {
        val points = PlaneShape.getPoints().toMutableList()
        points.addAll(InvertedPlaneShape.getPoints())
        points
    }
    override fun getUvs(): List<Vec2f> = run {
        val uvs = PlaneShape.getUvs().toMutableList()
        uvs.addAll(InvertedPlaneShape.getUvs())
        uvs
    }
    private val POINTS: List<Vec3d> = run {
        val points = PlaneShape.getPoints().toMutableList()
        points.addAll(InvertedPlaneShape.getPoints())
        points
    }

    private val UVS: List<Vec2f> = run {
        val uvs = PlaneShape.getUvs().toMutableList()
        uvs.addAll(InvertedPlaneShape.getUvs())
        uvs
    }
}