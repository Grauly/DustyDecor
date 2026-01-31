package grauly.dustydecor.geometry

import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

object InvertedPlaneShape: ShapeDefinition {
    override fun getPoints(): List<Vec3> = PlaneShape.getPoints().reversed()

    override fun getUvs(): List<Vec2> = PlaneShape.getUvs().reversed()
}