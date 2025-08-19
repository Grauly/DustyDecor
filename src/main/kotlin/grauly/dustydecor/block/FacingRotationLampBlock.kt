package grauly.dustydecor.block

import grauly.dustydecor.extensions.makeMaskVector
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.Direction

abstract class FacingRotationLampBlock(settings: Settings?) : FacingLampBlock(settings) {

    init {
        defaultState = defaultState
            .with(ROTATED, false)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val superState = super.getPlacementState(ctx)
        val targetBlockRelative = ctx.hitPos.subtract(ctx.blockPos.toCenterPos())
        val side = ctx.side
        val placementDirectionMaskVector = side.opposite.doubleVector.makeMaskVector()
        val projectionVector = targetBlockRelative.multiply(placementDirectionMaskVector)
        val impliedDirection = Direction.getFacing(projectionVector)
        val needsRotation = if (side == Direction.UP || side == Direction.DOWN) {
            impliedDirection != Direction.NORTH && impliedDirection != Direction.SOUTH
        } else {
            impliedDirection != Direction.UP && impliedDirection != Direction.DOWN
        }
        return superState.with(ROTATED, needsRotation)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(ROTATED)
    }

    companion object {
        val ROTATED: BooleanProperty = BooleanProperty.of("rotated")
    }
}