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
    val bias: Vec3i = ZERO_BIAS,
) {
    val layers: ArrayDeque<List<BlockPos>> = ArrayDeque(listOf(listOf(pos)))
    val visited: MutableSet<BlockPos> = HashSet(listOf(pos))

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
    fun floodLayer(
        level: LevelAccessor,
        predicate: (LevelAccessor, BlockPos, BlockState) -> Boolean,
        addCallback: (LevelAccessor, BlockPos, BlockState) -> Unit = DEFAULT_ADD
    ) {
        if (layers.isEmpty()) return
        val lastElements = layers.last()
        val collectionList = mutableListOf<BlockPos>()
        lastElements.forEach { blockPos ->
            val workingPos = blockPos.offset(bias)
            if (bias != ZERO_BIAS && !addIfNotVisited(collectionList, workingPos, level, predicate, addCallback)) return@forEach
            searchPositions.forEach { searchOffset ->
                addIfNotVisited(collectionList, workingPos.offset(searchOffset), level, predicate, addCallback)
            }
        }
        layers.add(collectionList)
    }

    /**
     * Run the flood fill until the abort condition is reached
     *
     * @param level The Level this floodfill takes place in
     * @param predicate A predicate of positions to include
     * @param abortPredicate The abort condition. NOTE: It will abort in any case if the last iteration added no new matches
     * By Default stops after 100 iterations
     */
    fun flood(
        level: LevelAccessor,
        predicate: (LevelAccessor, BlockPos, BlockState) -> Boolean,
        abortPredicate: (FloodFill) -> Boolean = DEFAULT_ABORT,
        addCallback: (LevelAccessor, BlockPos, BlockState) -> Unit = DEFAULT_ADD,
    ) {
        while (!(abortPredicate.invoke(this) || layers.last().isEmpty())) {
            floodLayer(level, predicate, addCallback)
        }
    }

    private fun addIfNotVisited(
        target: MutableList<BlockPos>,
        pos: BlockPos,
        level: LevelAccessor,
        predicate: (LevelAccessor, BlockPos, BlockState) -> Boolean,
        addCallback: (LevelAccessor, BlockPos, BlockState) -> Unit
    ): Boolean {
        if (visited.contains(pos)) return false
        visited.add(pos)
        val state = level.getBlockState(pos)
        if (!predicate.invoke(level, pos, state)) return false
        target.add(pos)
        addCallback.invoke(level, pos, state)
        return true
    }

    companion object {
        val ZERO_BIAS = Vec3i(0, 0, 0)
        val DEFAULT_ABORT = { floodFill: FloodFill -> floodFill.layers.size >= 25 }
        val DEFAULT_ADD = { level: LevelAccessor, pos: BlockPos, state: BlockState -> }
    }
}