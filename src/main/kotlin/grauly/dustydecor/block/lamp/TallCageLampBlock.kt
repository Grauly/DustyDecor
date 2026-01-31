package grauly.dustydecor.block.lamp

import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes

open class TallCageLampBlock(settings: Properties?) : FacingLampBlock(settings) {
    override fun getShape(state: BlockState): VoxelShape {
        return SHAPES[state.getValue(BlockStateProperties.FACING).opposite]!!
    }

    override fun isPathfindable(state: BlockState?, type: PathComputationType?): Boolean = false

    companion object {
        val SHAPES: Map<Direction, VoxelShape> = Shapes.rotateAll(
            Shapes.box(
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