package grauly.dustydecor.geometry

import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class BacksideBeamShape(
    private val length: Double,
    private val smallZ: Double,
    private val smallY: Double,
    private val largeZ: Double,
    private val largeY: Double
): ShapeDefinition {
    override fun getPoints(): List<Vec3> = points

    override fun getUvs(): List<Vec2> = UVS

    private val points = getPointsInternal()
    private val UVS = run {
        val points: MutableList<Vec2> = mutableListOf()
        for (i in 1..4) {
            points.addAll(PlaneShape.getUvs())
        }
        points
    }

    private fun getPointsInternal(): List<Vec3> {
        val smallUp = Vec3(0.0, smallY/2, 0.0)
        val bigUp = Vec3(0.0, largeY/2, 0.0)
        val smallLeft = Vec3(0.0, 0.0, smallZ/2)
        val bigLeft = Vec3(0.0, 0.0, largeZ/2)
        val forward = Vec3(length, 0.0, 0.0)
        val points: MutableList<Vec3> = mutableListOf(
            //bottom plane
            Vec3.ZERO.add(smallUp.reverse()).add(smallLeft.reverse()),
            Vec3.ZERO.add(smallUp.reverse()).add(smallLeft),
            forward.add(bigUp.reverse()).add(bigLeft),
            forward.add(bigUp.reverse()).add(bigLeft.reverse()),
            //left plane
            Vec3.ZERO.add(smallUp.reverse()).add(smallLeft),
            Vec3.ZERO.add(smallUp).add(smallLeft),
            forward.add(bigUp).add(bigLeft),
            forward.add(bigUp.reverse()).add(bigLeft),
            //top plane
            Vec3.ZERO.add(smallUp).add(smallLeft),
            Vec3.ZERO.add(smallUp).add(smallLeft.reverse()),
            forward.add(bigUp).add(bigLeft.reverse()),
            forward.add(bigUp).add(bigLeft),
            //right plane
            Vec3.ZERO.add(smallUp).add(smallLeft.reverse()),
            Vec3.ZERO.add(smallUp.reverse()).add(smallLeft.reverse()),
            forward.add(bigUp.reverse()).add(bigLeft.reverse()),
            forward.add(bigUp).add(bigLeft.reverse()),
        )
        return points
    }
}