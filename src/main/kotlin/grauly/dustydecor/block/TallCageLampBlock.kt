package grauly.dustydecor.block

import grauly.dustydecor.blockentity.TallCageLampBlockEntity
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.World

class TallCageLampBlock(settings: Settings?) : FacingLampBlock(settings), BlockEntityProvider {
    override fun getShape(state: BlockState): VoxelShape {
        return SHAPES[state.get(Properties.FACING).opposite]!!
    }

    override fun canPathfindThrough(state: BlockState?, type: NavigationType?): Boolean = false

    companion object {
        val SHAPES: Map<Direction, VoxelShape> = VoxelShapes.createFacingShapeMap(
            VoxelShapes.cuboid(
                5.0 / 16,
                5.0 / 16,
                0.0,
                11.0 / 16,
                11.0 / 16,
                9.0 / 16
            )
        )
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return TallCageLampBlockEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { world, pos, state, blockEntity ->
            if (!world.isClient) return@BlockEntityTicker
            if (blockEntity !is TallCageLampBlockEntity) return@BlockEntityTicker
            blockEntity.tick(world, pos, state, blockEntity)
        }
    }
}