package grauly.dustydecor.geometry

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

class BacksideBeamShape(
    private val length: Double,
    private val smallZ: Double,
    private val smallY: Double,
    private val largeZ: Double,
    private val largeY: Double
): ShapeDefinition {
    override fun getPoints(): List<Vec3d> = points

    override fun getUvs(): List<Vec2f> = UVS

    private val points = getPointsInternal()
    private val UVS = run {
        val points: MutableList<Vec2f> = mutableListOf()
        for (i in 1..4) {
            points.addAll(PlaneShape.getUvs())
        }
        points
    }

    private fun getPointsInternal(): List<Vec3d> {
        val smallUp = Vec3d(0.0, smallY/2, 0.0)
        val bigUp = Vec3d(0.0, largeY/2, 0.0)
        val smallLeft = Vec3d(0.0, 0.0, smallZ/2)
        val bigLeft = Vec3d(0.0, 0.0, largeZ/2)
        val forward = Vec3d(length, 0.0, 0.0)
        val points: MutableList<Vec3d> = mutableListOf(
            //bottom plane
            Vec3d.ZERO.add(smallUp.negate()).add(smallLeft.negate()),
            Vec3d.ZERO.add(smallUp.negate()).add(smallLeft),
            forward.add(bigUp.negate()).add(bigLeft),
            forward.add(bigUp.negate()).add(bigLeft.negate()),
            //left plane
            Vec3d.ZERO.add(smallUp.negate()).add(smallLeft),
            Vec3d.ZERO.add(smallUp).add(smallLeft),
            forward.add(bigUp).add(bigLeft),
            forward.add(bigUp.negate()).add(bigLeft),
            //top plane
            Vec3d.ZERO.add(smallUp).add(smallLeft),
            Vec3d.ZERO.add(smallUp).add(smallLeft.negate()),
            forward.add(bigUp).add(bigLeft.negate()),
            forward.add(bigUp).add(bigLeft),
            //right plane
            Vec3d.ZERO.add(smallUp).add(smallLeft.negate()),
            Vec3d.ZERO.add(smallUp.negate()).add(smallLeft.negate()),
            forward.add(bigUp.negate()).add(bigLeft.negate()),
            forward.add(bigUp).add(bigLeft.negate()),
        )
        return points
    }
}