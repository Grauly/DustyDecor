package grauly.dustydecor.block.lamp

import grauly.dustydecor.extensions.makeMaskVector
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.core.Direction

abstract class FacingRotationLampBlock(settings: Properties) : FacingLampBlock(settings) {

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(ROTATED, false)
        )
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val superState = super.getStateForPlacement(ctx)
        val targetBlockRelative = ctx.clickLocation.subtract(ctx.clickedPos.center)
        val side = ctx.clickedFace
        val placementDirectionMaskVector = side.opposite.unitVec3.makeMaskVector()
        val projectionVector = targetBlockRelative.multiply(placementDirectionMaskVector)
        val impliedDirection = Direction.getApproximateNearest(projectionVector)
        val needsRotation = if (side == Direction.UP || side == Direction.DOWN) {
            impliedDirection != Direction.NORTH && impliedDirection != Direction.SOUTH
        } else {
            impliedDirection != Direction.UP && impliedDirection != Direction.DOWN
        }
        return superState.setValue(ROTATED, needsRotation)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(ROTATED)
    }

    companion object {
        val ROTATED: BooleanProperty = BooleanProperty.create("rotated")
    }
}