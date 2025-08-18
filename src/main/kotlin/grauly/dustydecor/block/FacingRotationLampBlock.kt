package grauly.dustydecor.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.text.Text
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

abstract class FacingRotationLampBlock(settings: Settings?) : FacingLampBlock(settings) {

    init {
        defaultState = defaultState
            .with(ROTATED, false)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val superState = super.getPlacementState(ctx)
        val targetBlockRelative = ctx.hitPos.subtract(ctx.blockPos.toCenterPos())
        val side = ctx.side
        val placementDirectionMaskVector = makeMaskVector(side.opposite.doubleVector)
        val projectionVector = targetBlockRelative.multiply(placementDirectionMaskVector)
        val impliedDirection = Direction.getFacing(projectionVector)
        val needsRotation = if (side == Direction.UP || side == Direction.DOWN) {
            impliedDirection != Direction.NORTH && impliedDirection != Direction.SOUTH
        } else {
            impliedDirection != Direction.UP && impliedDirection != Direction.DOWN
        }
        return superState.with(ROTATED, needsRotation)
    }

    private fun makeMaskVector(vec: Vec3d): Vec3d {
        val x = makeMaskValue(vec.x)
        val y = makeMaskValue(vec.y)
        val z = makeMaskValue(vec.z)
        return Vec3d(x, y, z)
    }

    private fun makeMaskValue(number: Double): Double = if (number == 0.0) 1.0 else 0.0

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(ROTATED)
    }

    companion object {
        val ROTATED: BooleanProperty = BooleanProperty.of("rotated")
    }
}