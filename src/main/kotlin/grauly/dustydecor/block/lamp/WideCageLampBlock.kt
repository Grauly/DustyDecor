package grauly.dustydecor.block.lamp

import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes

class WideCageLampBlock(settings: Properties) : FacingRotationLampBlock(settings) {

    override fun getShape(state: BlockState): VoxelShape {
        return (SHAPES[state.getValue(ROTATED)]!!)[state.getValue(BlockStateProperties.FACING).opposite]!!
    }

    override fun isPathfindable(state: BlockState, type: PathComputationType): Boolean = false

    companion object {
        private val BASE_SHAPE: VoxelShape = Shapes.box(5.0/16, 4.0/16, 0.0, 11.0/16, 12.0/16, 5.0/16)
        private val ROTATED_SHAPE: VoxelShape = Shapes.box(4.0/16, 5.0/16, 0.0, 12.0/16, 11.0/16, 5.0/16)
        val SHAPES: Map<Boolean, Map<Direction, VoxelShape>> = (listOf(true, false).fold(mutableMapOf()) { acc, element ->
            acc[element] = Shapes.rotateAll(if (element) ROTATED_SHAPE else BASE_SHAPE); acc
        })
    }
}