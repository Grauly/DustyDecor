package grauly.dustydecor.block.furniture

import net.minecraft.world.phys.Vec3

class StoolBlock(settings: Properties) : SittableFurnitureBlock(settings) {
    override fun getSitOffset(): Vec3 = Vec3(.5, 9.0 / 16.0, .5)
}