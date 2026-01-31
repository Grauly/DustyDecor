package grauly.dustydecor.extensions

import net.minecraft.world.phys.Vec3

fun Vec3.makeMaskVector(): Vec3 {
    val x = makeMaskValue(this.x)
    val y = makeMaskValue(this.y)
    val z = makeMaskValue(this.z)
    return Vec3(x, y, z)
}

private fun makeMaskValue(number: Double): Double = if (number == 0.0) 1.0 else 0.0
