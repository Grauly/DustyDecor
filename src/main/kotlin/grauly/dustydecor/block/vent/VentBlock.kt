package grauly.dustydecor.block.vent

import grauly.dustydecor.ModBlockTags
import grauly.dustydecor.ModBlocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.level.pathfinder.PathComputationType
import com.mojang.math.Quadrant
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import com.mojang.math.OctahedralGroup
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader

class VentBlock(settings: Properties) : SideConnectableBlock(settings.dynamicShape().forceSolidOn()) {

    //TODO: add potion spreading logic
    //TODO: add smaller vents

    init {
        for (state in stateDefinition.possibleStates) {
            SHAPES[state] = run {
                var shape = FRAME_SHAPE
                for (direction: Direction in Direction.entries) {
                    if (state.getValueOrElse(getStateForDirection(direction), !FACE_CONNECTED) == !FACE_CONNECTED) {
                        shape = Shapes.or(shape, COVER_SHAPE_MAP[direction]!!)
                    }
                }
                shape
            }
        }
    }

    override fun canConnectTo(state: BlockState, pos: BlockPos, world: LevelReader, connectingSide: Direction): Boolean {
        if (!state.`is`(ModBlockTags.LARGE_VENT_CONNECTABLE)) return false
        if (state.`is`(ModBlocks.VENT_COVER)) {
            if (state.getValue(VentCoverBlock.COVERS_FACE).opposite != connectingSide) return false
        }
        return true
    }

    override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPES[state]!!
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return getShape(state, world, pos, context)
    }

    override fun isPathfindable(state: BlockState, type: PathComputationType): Boolean = false

    companion object {
        val SHAPES: MutableMap<BlockState, VoxelShape> = mutableMapOf()
        val COVER_SHAPE_MAP: Map<Direction, VoxelShape> =
            Shapes.rotateAll(Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0 / 16))
        val FRAME_SHAPE: VoxelShape = run {
            var fourPostShape = Shapes.empty()
            val singlePost = Shapes.box(0.0, 0.0, 0.0, 1.0 / 16, 1.0, 1.0 / 16)
            for (rotation: Quadrant in Quadrant.entries) {
                fourPostShape = Shapes.or(
                    fourPostShape,
                    Shapes.rotate(
                        singlePost,
                        Quadrant.fromXYAngles(Quadrant.R0, rotation)
                    )
                )
            }
            Shapes.or(
                fourPostShape,
                Shapes.rotate(
                    fourPostShape,
                    Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R0)
                ),
                Shapes.rotate(
                    fourPostShape,
                    Quadrant.fromXYAngles(Quadrant.R90, Quadrant.R90)
                )
            )
        }
    }
}