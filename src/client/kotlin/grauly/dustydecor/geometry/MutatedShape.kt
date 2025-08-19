package grauly.dustydecor.geometry

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d

class MutatedShape(private val points: List<Vec3d>, private val uvs: List<Vec2f>): ShapeDefinition {
    override fun getPoints(): List<Vec3d> = points
    override fun getUvs(): List<Vec2f> = uvs
}