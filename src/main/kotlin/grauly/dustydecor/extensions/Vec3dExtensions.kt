package grauly.dustydecor.extensions

import net.minecraft.util.math.Vec3d

fun Vec3d.makeMaskVector(): Vec3d {
    val x = makeMaskValue(this.x)
    val y = makeMaskValue(this.y)
    val z = makeMaskValue(this.z)
    return Vec3d(x, y, z)
}

private fun makeMaskValue(number: Double): Double = if (number == 0.0) 1.0 else 0.0
