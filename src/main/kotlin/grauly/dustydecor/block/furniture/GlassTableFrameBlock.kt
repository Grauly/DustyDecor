package grauly.dustydecor.block.furniture

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class GlassTableFrameBlock(settings: Properties) : RestrictedRotationFurnitureBlock(settings) {
    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return OUTLINE_SHAPE
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return COLLISION_SHAPE
    }

    override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return VISUAL_SHAPE
    }

    companion object {
        val OUTLINE_SHAPE: VoxelShape = column(14.0, 0.0, 15.0)
        val COLLISION_SHAPE: VoxelShape = column(16.0, 15.0, 16.0)
        val VISUAL_SHAPE: VoxelShape = Shapes.empty()
    }
}