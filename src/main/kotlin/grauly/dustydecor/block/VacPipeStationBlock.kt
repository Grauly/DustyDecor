package grauly.dustydecor.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.Waterloggable
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

class VacPipeStationBlock(settings: Settings?) : HorizontalFacingBlock(settings), Waterloggable {

    init {
        defaultState = defaultState
            .with(FACING, Direction.NORTH)
            .with(Properties.WATERLOGGED, false)
            .with(SENDING, false)
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
        if (state.get(Properties.WATERLOGGED, false)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }
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

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return super.getPlacementState(ctx)
            ?.with(FACING, ctx.verticalPlayerLookDirection)
            ?.with(Properties.WATERLOGGED, ctx.world.getFluidState(ctx.blockPos).fluid == Fluids.WATER)
    }

    override fun getCodec(): MapCodec<out HorizontalFacingBlock> {
        return createCodec(::VacPipeStationBlock)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(FACING, Properties.WATERLOGGED, SENDING)
    }

    companion object {
        val SENDING: BooleanProperty = BooleanProperty.of("sending")
    }
}