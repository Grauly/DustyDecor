package grauly.dustydecor.block.furniture

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.RotationSegment
import net.minecraft.util.RandomSource
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.FluidState

abstract class SingleFurnitureBlock(settings: Properties) : Block(settings) {
    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(ROTATION, 0)
        )
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val fluidState = ctx.level.getFluidState(ctx.clickedPos)
        return defaultBlockState()
            .setValue(WATERLOGGED, fluidState.`is`(Fluids.WATER))
            .setValue(ROTATION, RotationSegment.convertToSegment(ctx.rotation))
    }

    override fun updateShape(
        state: BlockState,
        level: LevelReader,
        tickView: ScheduledTickAccess,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: RandomSource
    ): BlockState {
        if (state.getValue(WATERLOGGED)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
        if (!canSurvive(state, level, pos)) return Blocks.AIR.defaultBlockState()
        return super.updateShape(
            state,
            level,
            tickView,
            pos,
            direction,
            neighborPos,
            neighborState,
            random
        )
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            Fluids.WATER.getSource(false)
        } else {
            super.getFluidState(state)
        }
    }

    override fun mirror(
        state: BlockState,
        mirror: Mirror
    ): BlockState {
        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), 16))
    }

    override fun rotate(
        state: BlockState,
        rotation: Rotation
    ): BlockState {
        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), 16))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(WATERLOGGED, ROTATION)
    }

    companion object {
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
        val ROTATION: IntegerProperty = BlockStateProperties.ROTATION_16
    }
}