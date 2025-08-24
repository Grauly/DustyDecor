package grauly.dustydecor.geometry

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import kotlin.math.PI

object AlertBeamsShape : ShapeDefinition {
    override fun getPoints(): List<Vec3d> = POINTS

    override fun getUvs(): List<Vec2f> = UVS

    private val POINTS = run {
        val beam = BacksideBeamShape(
            25.0/16,
            4.0/16,
            6.0/16,
            8.0/16,
            10.0/16
        )
        val pointsA = beam.getPoints()
        val pointsB = beam.getTransformed(rotation = Quaternionf().rotationY(PI.toFloat())).getPoints()
        listOf(*pointsA.toTypedArray(), *pointsB.toTypedArray())
    }

    private val UVS = run {
        val beam = BacksideBeamShape(
            1.0,
            4.0/16,
            6.0/16,
            8.0/16,
            10.0/16
        ).getTransformed(Vec3d(2.0/16, 0.0, 0.0))
        listOf(
            *beam.getUvs().toTypedArray(),
            *beam.getUvs().toTypedArray()
        )
    }
}