package grauly.dustydecor.block.furniture

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class StoolBlock(settings: Properties) : SittableFurnitureBlock(settings) {
    override fun getSitOffset(state: BlockState): Vec3 = Vec3(.5, 9.0 / 16.0, .5)

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