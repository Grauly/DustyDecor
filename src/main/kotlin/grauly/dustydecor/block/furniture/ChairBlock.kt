package grauly.dustydecor.block.furniture

import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

class ChairBlock(settings: Properties) : SittableFurnitureBlock(settings) {
    override fun getSitOffset(state: BlockState): Vec3 = Vec3(.5, 9.0 / 16.0, .5)


}