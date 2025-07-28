package grauly.dustydecor.block

import grauly.dustydecor.mixin.TrapdoorBlockFlipInvoker
import net.minecraft.block.BlockSetType
import net.minecraft.block.BlockState
import net.minecraft.block.TrapdoorBlock
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random

class VentCoverBlock(settings: Settings): TrapdoorBlock(BlockSetType.COPPER, settings) {
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (state.get(OPEN)) {
            (this as TrapdoorBlockFlipInvoker).flip(state, world, pos, null)
        }
    }

    override fun hasRandomTicks(state: BlockState): Boolean {
        return super.hasRandomTicks(state) && !state.get(POWERED, false) && state.get(OPEN)
    }
}