package grauly.dustydecor.block.lamp

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess

abstract class FacingLampBlock(settings: Properties) : LightingFixtureBlock(settings) {
    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP)
        )
    }

    override fun updateShape(
        state: BlockState,
        world: LevelReader,
        tickView: ScheduledTickAccess,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: RandomSource
    ): BlockState {
        if (!canSurvive(state, world, pos)) return Blocks.AIR.defaultBlockState()
        return super.updateShape(
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

    override fun canSurvive(state: BlockState, world: LevelReader, pos: BlockPos): Boolean {
        val direction = state.getValue(BlockStateProperties.FACING)
        val checkState = world.getBlockState(pos.relative(direction.opposite))
        return checkState.isFaceSturdy(world, pos, direction)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val superState = super.getStateForPlacement(ctx)!!
        return superState.setValue(BlockStateProperties.FACING, ctx.clickedFace)
    }

    abstract fun getShape(state: BlockState): VoxelShape

    override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return getShape(state)
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return getShape(state)
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.setValue(BlockStateProperties.FACING, mirror.mirror(state.getValue(BlockStateProperties.FACING)))
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(BlockStateProperties.FACING, rotation.rotate(state.getValue(BlockStateProperties.FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(BlockStateProperties.FACING)
    }
}