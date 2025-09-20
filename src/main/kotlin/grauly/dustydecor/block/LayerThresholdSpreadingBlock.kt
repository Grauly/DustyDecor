package grauly.dustydecor.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.SnowBlock
import net.minecraft.item.AutomaticItemPlacementContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldView

class LayerThresholdSpreadingBlock(private val threshold: Int, settings: Settings?) : SnowBlock(settings) {

    override fun randomTick(state: BlockState?, world: ServerWorld?, pos: BlockPos?, random: Random?) {
        //dont do anything.
    }

    override fun onBlockAdded(
        state: BlockState,
        world: World,
        pos: BlockPos,
        oldState: BlockState,
        notify: Boolean
    ) {
        super.onBlockAdded(state, world, pos, oldState, notify)
        if (world.isClient) return
        trySpread(pos, world, state)
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val placedOnState = world.getBlockState(pos.down())
        return Block.isFaceFullSquare(placedOnState.getCollisionShape(world, pos.down()), Direction.UP) ||
                placedOnState.isOf(this) && placedOnState.get(LAYERS) == MAX_LAYERS
    }

    override fun canReplace(state: BlockState, context: ItemPlacementContext): Boolean {
        if (context.stack.isOf(this.asItem())) {
            return (state.get(LAYERS) < MAX_LAYERS)
        }
        return false
    }

    private fun trySpread(pos: BlockPos, world: World, state: BlockState) {
        val layers = state.get(LAYERS)
        val spreadTargets: List<Pair<Direction, Int>> = Direction.entries.filter { it.axis.isHorizontal }.map {
            val offset = pos.offset(it)
            val offsetState = world.getBlockState(offset)
            val canPlace = offsetState.canPlaceAt(world, offset)
            val canReplace = offsetState.canReplace(AutomaticItemPlacementContext(world, offset, it, this.asItem().defaultStack, it.opposite))
            if (canPlace && canReplace) {
                if (offsetState.isOf(this)) {
                    return@map it to offsetState.get(LAYERS)
                }
                return@map it to 0
            }
            return@map null
        }.filterNotNull()
        if (spreadTargets.isEmpty()) return
        var remainingLayers = layers
        val layerUpdates = mutableMapOf<Direction, Int>()
        for (i in 1..layers) {
            val smallestHeight: Pair<Direction, Int> = spreadTargets.minByOrNull { it.second }!!
            if (smallestHeight.second >= remainingLayers + threshold) break
            val allSimilarDifferentials: List<Pair<Direction, Int>> = spreadTargets.filter { it.second == smallestHeight.second }
            val pick = allSimilarDifferentials.random()
            layerUpdates.compute(pick.first) { _, l -> l?.inc() ?: 1 }
            remainingLayers -= 1
        }
        layerUpdates.forEach layerUpdates@{ (dir, l) ->
            val offsetPos = pos.offset(dir)
            val offsetState = world.getBlockState(offsetPos)
            if (offsetState.isOf(this)) {
                val existingLayers = offsetState.get(LAYERS)
                world.setBlockState(offsetPos, offsetState.with(LAYERS, existingLayers + l))
                return@layerUpdates
            }
            world.setBlockState(offsetPos, defaultState.with(LAYERS, l))
        }
        world.setBlockState(pos, state.with(LAYERS, remainingLayers))
    }
}