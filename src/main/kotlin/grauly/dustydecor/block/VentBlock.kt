package grauly.dustydecor.block

import net.minecraft.block.BlockState

class VentBlock(settings: Settings) : SideConnectableBlock(settings) {
    override fun canConnectTo(state: BlockState): Boolean {
        return state.block is VentBlock
    }
}