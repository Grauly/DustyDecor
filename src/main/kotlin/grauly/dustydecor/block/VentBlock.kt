package grauly.dustydecor.block

import grauly.dustydecor.ModBlockTags
import grauly.dustydecor.ModBlocks
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.AxisRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.DirectionTransformation
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldView

class VentBlock(settings: Settings) : SideConnectableBlock(settings.dynamicBounds().solid()) {

    init {
        for (state in stateManager.states) {
            SHAPES[state] = run {
                var shape = FRAME_SHAPE
                for (direction: Direction in Direction.entries) {
                    if (state.get(getStateForDirection(direction), !FACE_CONNECTED) == !FACE_CONNECTED) {
                        shape = VoxelShapes.union(shape, COVER_SHAPE_MAP[direction])
                    }
                }
                shape
            }
        }
    }

    override fun canConnectTo(state: BlockState, pos: BlockPos, world: WorldView, connectingSide: Direction): Boolean {
        if (!state.isIn(ModBlockTags.LARGE_VENT_CONNECTABLE)) return false
        if (state.isOf(ModBlocks.VENT_COVER)) {
            if (state.get(VentCoverBlock.COVERS_FACE).opposite != connectingSide) return false
        }
        return true
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return SHAPES[state]!!
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return getOutlineShape(state, world, pos, context)
    }

    companion object {
        val SHAPES: MutableMap<BlockState, VoxelShape> = mutableMapOf()
        val COVER_SHAPE_MAP: Map<Direction, VoxelShape> =
            VoxelShapes.createFacingShapeMap(VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0 / 16))
        val FRAME_SHAPE: VoxelShape = run {
            var fourPostShape = VoxelShapes.empty()
            val singlePost = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0 / 16, 1.0, 1.0 / 16)
            for (rotation: AxisRotation in AxisRotation.entries) {
                fourPostShape = VoxelShapes.union(
                    fourPostShape,
                    VoxelShapes.transform(
                        singlePost,
                        DirectionTransformation.fromRotations(AxisRotation.R0, rotation)
                    )
                )
            }
            VoxelShapes.union(
                fourPostShape,
                VoxelShapes.transform(
                    fourPostShape,
                    DirectionTransformation.fromRotations(AxisRotation.R90, AxisRotation.R0)
                ),
                VoxelShapes.transform(
                    fourPostShape,
                    DirectionTransformation.fromRotations(AxisRotation.R90, AxisRotation.R90)
                )
            )
        }
    }
}