package grauly.dustydecor.block

import net.minecraft.block.BlockState
import net.minecraft.block.SnowBlock
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random

class LayerThresholdSpreadingBlock(private val threshold: Int, settings: Settings?) : SnowBlock(settings) {

    override fun randomTick(state: BlockState?, world: ServerWorld?, pos: BlockPos?, random: Random?) {
        //dont do anything.
    }
}