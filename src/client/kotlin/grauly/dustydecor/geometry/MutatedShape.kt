package grauly.dustydecor.geometry

import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class MutatedShape(private val points: List<Vec3>, private val uvs: List<Vec2>): ShapeDefinition {
    override fun getPoints(): List<Vec3> = points
    override fun getUvs(): List<Vec2> = uvs
}