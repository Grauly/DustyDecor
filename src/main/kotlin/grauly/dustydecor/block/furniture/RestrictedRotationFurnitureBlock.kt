package grauly.dustydecor.block.furniture

import net.minecraft.core.Direction
import net.minecraft.util.Mth.clamp
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids

open class RestrictedRotationFurnitureBlock(settings: Properties) : SingleFurnitureBlock(settings) {
    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val fluidState = ctx.level.getFluidState(ctx.clickedPos)
        val rotationOrdinal = when (ctx.horizontalDirection.opposite) {
            Direction.NORTH -> 0
            Direction.EAST -> 4
            Direction.SOUTH -> 8
            Direction.WEST -> 12
            else -> 0
        }
        return defaultBlockState()
            .setValue(WATERLOGGED, fluidState.`is`(Fluids.WATER))
            .setValue(ROTATION, rotationOrdinal)
    }
}