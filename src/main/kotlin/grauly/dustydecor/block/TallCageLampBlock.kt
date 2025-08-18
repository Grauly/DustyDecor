package grauly.dustydecor.block

import net.minecraft.block.BlockState
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

class TallCageLampBlock(settings: Settings?) : FacingLampBlock(settings) {
    override fun getShape(state: BlockState): VoxelShape {
        return SHAPES[state.get(Properties.FACING).opposite]!!
    }

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
}