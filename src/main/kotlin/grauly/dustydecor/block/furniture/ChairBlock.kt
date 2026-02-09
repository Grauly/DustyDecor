package grauly.dustydecor.block.furniture

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class ChairBlock(settings: Properties) : SittableFurnitureBlock(settings) {
    override fun getSitOffset(state: BlockState): Vec3 = Vec3(.5, 9.0 / 16.0, .5)

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
        val OUTLINE_SHAPE: VoxelShape = column(16.0, 0.0, 14.0)
        val COLLISION_SHAPE: VoxelShape = column(12.0, 7.0, 9.0)
        val VISUAL_SHAPE: VoxelShape = column(12.0, 0.0, 9.0)
    }
}