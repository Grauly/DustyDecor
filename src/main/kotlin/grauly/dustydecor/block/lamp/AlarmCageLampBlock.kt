package grauly.dustydecor.block.lamp

import grauly.dustydecor.blockentity.AlarmCageLampBlockEntity
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

class AlarmCageLampBlock(settings: Properties?) : TallCageLampBlock(settings), EntityBlock {

    //TODO: add proper activation sound

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return AlarmCageLampBlockEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        world: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { world, pos, state, blockEntity ->
            if (!world.isClientSide) return@BlockEntityTicker
            if (blockEntity !is AlarmCageLampBlockEntity) return@BlockEntityTicker
            blockEntity.tick(world, pos, state, blockEntity)
        }
    }
}