package grauly.dustydecor.geometry

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

object PlaneShape : ShapeDefinition {
    override fun getPoints(): List<Vec3d> = POINTS

    override fun getUvs(): List<Vec2f> = UVS

    private val POINTS: List<Vec3d> = listOf(
        Vec3d(-0.5, 0.0, -0.5),
        Vec3d(-0.5, 0.0, 0.5),
        Vec3d(0.5, 0.0, 0.5),
        Vec3d(0.5, 0.0, -0.5),
    )
    private val UVS: List<Vec2f> = listOf(
        Vec2f(1f, 1f),
        Vec2f(1f, 0f),
        Vec2f(0f, 0f),
        Vec2f(0f, 1f),
    )
}