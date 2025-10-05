package grauly.dustydecor.block

import net.minecraft.block.BlockState
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

class TubeLampBlock(settings: Settings?) : FacingRotationLampBlock(settings) {

    //TODO: add proper sounds

    override fun getShape(state: BlockState): VoxelShape {
        return (SHAPES[state.get(ROTATED)]!!)[state.get(Properties.FACING).opposite]!!
    }

    override fun canPathfindThrough(
        state: BlockState?,
        type: NavigationType?
    ): Boolean = true

    companion object {
        private val BASE_SHAPE: VoxelShape = VoxelShapes.cuboid(7.0/16, 0.0/16, 0.0/16, 9.0/16, 16.0/16, 2.0/16)
        private val ROTATED_SHAPE: VoxelShape = VoxelShapes.cuboid(0.0/16, 7.0/16, 0.0/16, 16.0/16, 9.0/16, 2.0/16)
        val SHAPES: Map<Boolean, Map<Direction, VoxelShape>> = (listOf(true, false).fold(mutableMapOf()) { acc, element ->
            acc[element] = VoxelShapes.createFacingShapeMap(if (element) ROTATED_SHAPE else BASE_SHAPE); acc
        })
    }
}