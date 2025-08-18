package grauly.dustydecor.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

class TallCageLampBlock(settings: Settings?) : LightingFixtureBlock(settings) {
    init {
        defaultState = defaultState
            .with(Properties.FACING, Direction.UP)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: Random
    ): BlockState {
        if (!canPlaceAt(state, world, pos)) return Blocks.AIR.defaultState
        return super.getStateForNeighborUpdate(
            state,
            world,
            tickView,
            pos,
            direction,
            neighborPos,
            neighborState,
            random
        )
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val direction = state.get(Properties.FACING)
        val checkState = world.getBlockState(pos.offset(direction.opposite))
        return checkState.isSideSolidFullSquare(world, pos, direction)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val superState = super.getPlacementState(ctx)!!
        return superState.with(Properties.FACING, ctx.side)
    }

    private fun getShape(state: BlockState): VoxelShape {
        return SHAPES[state.get(Properties.FACING).opposite]!!
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return getShape(state)
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return getShape(state)
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return state.with(Properties.FACING, mirror.apply(state.get(Properties.FACING)))
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)))
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(Properties.FACING)
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