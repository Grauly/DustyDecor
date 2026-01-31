package grauly.dustydecor.geometry

import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

object PlaneShape : ShapeDefinition {
    override fun getPoints(): List<Vec3> = POINTS

    override fun getUvs(): List<Vec2> = UVS

    private val POINTS: List<Vec3> = listOf(
        Vec3(-0.5, 0.0, -0.5),
        Vec3(-0.5, 0.0, 0.5),
        Vec3(0.5, 0.0, 0.5),
        Vec3(0.5, 0.0, -0.5),
    )
    private val UVS: List<Vec2> = listOf(
        Vec2(1f, 1f),
        Vec2(1f, 0f),
        Vec2(0f, 0f),
        Vec2(0f, 1f),
    )
}