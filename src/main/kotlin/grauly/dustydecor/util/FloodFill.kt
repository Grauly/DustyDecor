package grauly.dustydecor.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState

/**
 * Represents a Flood Fill run.
 *
 * @property layers collects all found locations, with every list representing a further iterations,
 * thus giving a estimation of distance.
 *
 * @param bias Represents the general direction the floodfill will head to. A bias of (0,0,0) is a standard flood fill.
 * If the bias is diagonal or has a magnitude > 1, this allows for flood fill escapes.
 * Setting bias will lead to potential fill locations not being filled
 * @param pos The starting position for the floodfill. Note that the Level is supplied later
 */
class FloodFill(
    val pos: BlockPos,
    val bias: Vec3i = Vec3i(0, 0, 0),
) {
    val layers: ArrayDeque<List<BlockPos>> = ArrayDeque(listOf(listOf(pos)))
    val visited: MutableSet<BlockPos> = HashSet()

    val searchPositions: List<Vec3i> = listOf(
        Vec3i(1, 0, 0),
        Vec3i(0, 1, 0),
        Vec3i(0, 0, 1),
        Vec3i(-1, 0, 0),
        Vec3i(0, -1, 0),
        Vec3i(0, 0, -1),
    ).sortedBy { it.distSqr(bias) }

    /**
     * Run a flood fill iteration. The result is directly put as a new layer into the layers.
     *
     * @param level The Level this iteration runs in
     * @param predicate A predicate of positions to include
     */
    fun floodLayer(level: LevelAccessor, predicate: (LevelAccessor, BlockPos, BlockState) -> Boolean) {
        if (layers.isEmpty()) return
        val lastElements = layers.last()
        val collectionList = mutableListOf<BlockPos>()
        lastElements.forEach { blockPos ->
            val offsetPos = blockPos.offset(bias)
            addIfNotVisited(collectionList, offsetPos, level, predicate)
            searchPositions.forEach { pos ->
                addIfNotVisited(collectionList, offsetPos.offset(pos), level, predicate)
            }
        }
        layers.add(collectionList)
    }

    /**
     * Run the flood fill until the abort condition is reached
     *
     * @param level The Level this floodfill takes place in
     * @param predicate A predicate of positions to include
     * @param abortPredicate The abort condition. NOTE: If not properly specified, this WILL run forever.
     */
    fun flood(level: LevelAccessor, predicate: (LevelAccessor, BlockPos, BlockState) -> Boolean, abortPredicate: (FloodFill) -> Boolean = DEFAULT_ABORT) {
        while (!abortPredicate.invoke(this)) {
            floodLayer(level, predicate)
        }
    }

    private fun addIfNotVisited(
        target: MutableList<BlockPos>,
        pos: BlockPos,
        level: LevelAccessor,
        predicate: (LevelAccessor, BlockPos, BlockState) -> Boolean
    ) {
        if (visited.contains(pos)) return
        visited.add(pos)
        if (!predicate.invoke(level, pos, level.getBlockState(pos))) return
        target.add(pos)
    }

    companion object {
        val DEFAULT_ABORT = { floodFill: FloodFill -> floodFill.layers.size >= 100 || floodFill.layers.last().isEmpty() }
    }
}