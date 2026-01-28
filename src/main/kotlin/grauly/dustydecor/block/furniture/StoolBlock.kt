package grauly.dustydecor.block.furniture

import net.minecraft.util.math.Vec3d

class StoolBlock(settings: Settings) : SittableFurnitureBlock(settings) {
    override fun getSitOffset(): Vec3d = Vec3d(.5, 9.0 / 16.0, .5)
}