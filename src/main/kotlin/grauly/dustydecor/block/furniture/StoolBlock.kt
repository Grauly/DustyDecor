package grauly.dustydecor.block.furniture

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
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

    companion object {
        val BASE_SHAPE: VoxelShape =
            Shapes.or(
                Shapes.box(0.25, 0.4375, 0.25, 0.75, 0.5625, 0.75),
                Shapes.box(0.1875, 0.0, 0.3125, 0.25, 0.5625, 0.375),
                Shapes.box(0.1875, 0.5, 0.375, 0.25, 0.5625, 0.6875),
                Shapes.box(0.25, 0.375, 0.3125, 0.5, 0.4375, 0.375),
                Shapes.box(0.25, 0.375, 0.625, 0.5, 0.4375, 0.6875),
                Shapes.box(0.1875, 0.0, 0.375, 0.25, 0.0625, 0.75),
                Shapes.box(0.25, 0.0, 0.6875, 0.5, 0.0625, 0.75),
                Shapes.box(0.1875, 0.375, 0.625, 0.25, 0.5, 0.6875),
                Shapes.box(0.75, 0.0, 0.3125, 0.8125, 0.5625, 0.375),
                Shapes.box(0.75, 0.5, 0.375, 0.8125, 0.5625, 0.6875),
                Shapes.box(0.75, 0.375, 0.625, 0.8125, 0.5, 0.6875),
                Shapes.box(0.75, 0.0, 0.375, 0.8125, 0.0625, 0.75),
                Shapes.box(0.5, 0.375, 0.3125, 0.75, 0.4375, 0.375),
                Shapes.box(0.5, 0.0, 0.6875, 0.75, 0.0625, 0.75),
                Shapes.box(0.5, 0.375, 0.625, 0.75, 0.4375, 0.6875)
            )
    }
}