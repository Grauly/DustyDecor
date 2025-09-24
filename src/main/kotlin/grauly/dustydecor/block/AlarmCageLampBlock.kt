package grauly.dustydecor.block

import grauly.dustydecor.blockentity.AlarmCageLampBlockEntity
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class AlarmCageLampBlock(settings: Settings?) : TallCageLampBlock(settings), BlockEntityProvider {

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return AlarmCageLampBlockEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { world, pos, state, blockEntity ->
            if (!world.isClient) return@BlockEntityTicker
            if (blockEntity !is AlarmCageLampBlockEntity) return@BlockEntityTicker
            blockEntity.tick(world, pos, state, blockEntity)
        }
    }
}