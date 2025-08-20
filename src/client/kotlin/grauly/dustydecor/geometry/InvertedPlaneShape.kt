package grauly.dustydecor.geometry

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

object InvertedPlaneShape: ShapeDefinition {
    override fun getPoints(): List<Vec3d> = PlaneShape.getPoints().reversed()

    override fun getUvs(): List<Vec2f> = PlaneShape.getUvs().reversed()
}