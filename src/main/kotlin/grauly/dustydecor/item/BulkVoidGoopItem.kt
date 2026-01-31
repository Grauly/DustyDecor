package grauly.dustydecor.item

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModComponentTypes
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.voidgoop.LayerThresholdSpreadingBlock
import net.minecraft.world.item.context.DirectionalPlaceContext
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import kotlin.math.min

class BulkVoidGoopItem(settings: Properties?) : Item(settings) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val pos = context.clickedPos.relative(context.clickedFace)
        val goopToPlace = context.itemInHand.get(ModComponentTypes.VOID_GOOP_SIZE)?.size ?: 0
        val results = findPlacementLocations(pos, context.level, goopToPlace).filterValues { it > 0 }
        val actuallyPlaced = results.values.reduceOrNull { acc, i -> acc + i }
        context.player?.displayClientMessage(Component.translatable(VOID_GOOP_PLACED, actuallyPlaced ?: 0, ModBlocks.VOID_GOOP.name), true)
        placeVoidGoop(context.level, results)
        return InteractionResult.SUCCESS
    }

    private fun placeVoidGoop(world: Level, placementLocations: Map<BlockPos, Int>) {
        placementLocations.forEach { pos, layers ->
            val existingState = world.getBlockState(pos)
            if (existingState.`is`(ModBlocks.VOID_GOOP)) {
                world.setBlockAndUpdate(
                    pos,
                    existingState.setValue(
                        LayerThresholdSpreadingBlock.LAYERS,
                        existingState.getValue(LayerThresholdSpreadingBlock.LAYERS) + layers
                    )
                )
            } else {
                world.setBlockAndUpdate(
                    pos,
                    ModBlocks.VOID_GOOP.defaultBlockState().setValue(LayerThresholdSpreadingBlock.LAYERS, layers)
                )
            }
        }
    }

    private fun findPlacementLocations(startPos: BlockPos, world: Level, goopToPlace: Int): Map<BlockPos, Int> {
        var remainingGoop: Int = goopToPlace
        val results = mutableMapOf<BlockPos, Int>()
        val locationQueue = ArrayDeque<BlockPos>()
        locationQueue.add(startPos)
        while (remainingGoop > 0 && locationQueue.isNotEmpty()) {
            val pos = locationQueue.removeFirst()
            if (results.contains(pos)) {
                continue
            }
            val state = world.getBlockState(pos)
            val canReplace = state.canBeReplaced(
                DirectionalPlaceContext(
                    world,
                    pos,
                    Direction.DOWN,
                    ModItems.VOID_GOOP.defaultInstance,
                    Direction.UP
                )
            )
            if (!canReplace) {
                results[pos] = 0
                continue
            }
            if (state.`is`(ModBlocks.VOID_GOOP)) {
                val existingLayers = state.getValue(LayerThresholdSpreadingBlock.LAYERS)
                val possibleLayers = LayerThresholdSpreadingBlock.MAX_LAYERS - existingLayers
                val placeableLayers = min(possibleLayers, goopToPlace)
                remainingGoop -= placeableLayers
                results[pos] = placeableLayers
            } else {
                val placeableLayers = min(remainingGoop, LayerThresholdSpreadingBlock.MAX_LAYERS)
                remainingGoop -= placeableLayers
                results[pos] = placeableLayers
            }
            addIfNotVisited(pos.below(), locationQueue, results)
            addIfNotVisited(pos.north(), locationQueue, results)
            addIfNotVisited(pos.south(), locationQueue, results)
            addIfNotVisited(pos.west(), locationQueue, results)
            addIfNotVisited(pos.east(), locationQueue, results)
            addIfNotVisited(pos.above(), locationQueue, results)
        }
        return results
    }

    private fun addIfNotVisited(pos: BlockPos, queue: ArrayDeque<BlockPos>, visited: Map<BlockPos, Int>) {
        if (visited.contains(pos)) return
        queue.add(pos)
    }

    companion object {
        const val VOID_GOOP_PLACED = "item.bulk_void_goop.placed"
    }
}