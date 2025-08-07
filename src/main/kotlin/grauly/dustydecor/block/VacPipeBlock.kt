package grauly.dustydecor.block

import grauly.dustydecor.ModBlocks
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldView

class VacPipeBlock(settings: Settings) : AbConnectableBlock(settings) {
    override fun isConnectable(
        state: BlockState,
        pos: BlockPos,
        world: WorldView,
        connectionDirection: Direction
    ): Boolean {
        return state.isOf(ModBlocks.VAC_PIPE)
    }
}