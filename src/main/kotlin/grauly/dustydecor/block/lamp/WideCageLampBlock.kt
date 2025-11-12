package grauly.dustydecor.block.lamp

import net.minecraft.block.BlockState
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

class WideCageLampBlock(settings: Settings?) : FacingRotationLampBlock(settings) {

    override fun getShape(state: BlockState): VoxelShape {
        return (SHAPES[state.get(ROTATED)]!!)[state.get(Properties.FACING).opposite]!!
    }

    override fun canPathfindThrough(state: BlockState?, type: NavigationType?): Boolean = false

    companion object {
        private val BASE_SHAPE: VoxelShape = VoxelShapes.cuboid(5.0/16, 4.0/16, 0.0, 11.0/16, 12.0/16, 5.0/16)
        private val ROTATED_SHAPE: VoxelShape = VoxelShapes.cuboid(4.0/16, 5.0/16, 0.0, 12.0/16, 11.0/16, 5.0/16)
        val SHAPES: Map<Boolean, Map<Direction, VoxelShape>> = (listOf(true, false).fold(mutableMapOf()) { acc, element ->
            acc[element] = VoxelShapes.createFacingShapeMap(if (element) ROTATED_SHAPE else BASE_SHAPE); acc
        })
    }
}