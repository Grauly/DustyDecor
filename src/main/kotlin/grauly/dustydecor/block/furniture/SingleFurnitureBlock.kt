package grauly.dustydecor.block.furniture

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationPropertyHelper
import net.minecraft.util.math.random.Random
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

abstract class SingleFurnitureBlock(settings: Settings) : Block(settings) {
    init {
        defaultState = defaultState
            .with(WATERLOGGED, false)
            .with(ROTATION, 0)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val fluidState = ctx.world.getFluidState(ctx.blockPos)
        return defaultState
            .with(WATERLOGGED, fluidState.isOf(Fluids.WATER))
            .with(ROTATION, RotationPropertyHelper.fromYaw(ctx.playerYaw + 180f))
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
    ): BlockState? {
        if (state.get(WATERLOGGED)) {
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

    override fun mirror(
        state: BlockState,
        mirror: BlockMirror
    ): BlockState? {
        return state.with(ROTATION, mirror.mirror(state.get(ROTATION), 16))
    }

    override fun rotate(
        state: BlockState,
        rotation: BlockRotation
    ): BlockState? {
        return state.with(ROTATION, rotation.rotate(state.get(ROTATION), 16))
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>?) {
        super.appendProperties(builder)
        builder?.add(WATERLOGGED, ROTATION)
    }

    companion object {
        val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
        val ROTATION: IntProperty = Properties.ROTATION
    }
}