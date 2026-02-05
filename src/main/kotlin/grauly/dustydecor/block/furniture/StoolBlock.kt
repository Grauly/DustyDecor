package grauly.dustydecor.block.furniture

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class StoolBlock(settings: Properties) : SittableFurnitureBlock(settings) {
    override fun getSitOffset(): Vec3 = Vec3(.5, 9.0 / 16.0, .5)

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return BASE_SHAPE
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return COLLISION_SHAPE
    }

    companion object {
        val BASE_SHAPE: VoxelShape = column(8.0, .0, 9.0)
        val COLLISION_SHAPE: VoxelShape = column(8.0, 7.0, 9.0)
    }
}