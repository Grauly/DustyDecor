package grauly.dustydecor.geometry

import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

object BiPlaneShape: ShapeDefinition {
    override fun getPoints(): List<Vec3> = run {
        val points = PlaneShape.getPoints().toMutableList()
        points.addAll(InvertedPlaneShape.getPoints())
        points
    }
    override fun getUvs(): List<Vec2> = run {
        val uvs = PlaneShape.getUvs().toMutableList()
        uvs.addAll(InvertedPlaneShape.getUvs())
        uvs
    }
    private val POINTS: List<Vec3> = run {
        val points = PlaneShape.getPoints().toMutableList()
        points.addAll(InvertedPlaneShape.getPoints())
        points
    }

    private val UVS: List<Vec2> = run {
        val uvs = PlaneShape.getUvs().toMutableList()
        uvs.addAll(InvertedPlaneShape.getUvs())
        uvs
    }
}