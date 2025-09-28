package grauly.dustydecor.item

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModComponentTypes
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.LayerThresholdSpreadingBlock
import net.minecraft.item.AutomaticItemPlacementContext
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import kotlin.math.min

class BulkVoidGoopItem(settings: Settings?) : Item(settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val pos = context.blockPos.offset(context.side)
        val goopToPlace = context.stack.get(ModComponentTypes.VOID_GOOP_SIZE)?.size ?: 0
        val results = findPlacementLocations(pos, context.world, goopToPlace).filterValues { it > 0 }
        val actuallyPlaced = results.values.reduce { acc, i -> acc + i }
        context.player?.sendMessage(Text.translatable(VOID_GOOP_PLACED, actuallyPlaced, ModBlocks.VOID_GOOP.name), true)
        placeVoidGoop(context.world, results)
        return ActionResult.SUCCESS
    }

    private fun placeVoidGoop(world: World, placementLocations: Map<BlockPos, Int>) {
        placementLocations.forEach { pos, layers ->
            val existingState = world.getBlockState(pos)
            if (existingState.isOf(ModBlocks.VOID_GOOP)) {
                world.setBlockState(
                    pos,
                    existingState.with(
                        LayerThresholdSpreadingBlock.LAYERS,
                        existingState.get(LayerThresholdSpreadingBlock.LAYERS) + layers
                    )
                )
            } else {
                world.setBlockState(
                    pos,
                    ModBlocks.VOID_GOOP.defaultState.with(LayerThresholdSpreadingBlock.LAYERS, layers)
                )
            }
        }
    }

    private fun findPlacementLocations(startPos: BlockPos, world: World, goopToPlace: Int): Map<BlockPos, Int> {
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
            val canReplace = state.canReplace(
                AutomaticItemPlacementContext(
                    world,
                    pos,
                    Direction.DOWN,
                    ModItems.VOID_GOOP.defaultStack,
                    Direction.UP
                )
            )
            if (!canReplace) {
                results[pos] = 0
                continue
            }
            if (state.isOf(ModBlocks.VOID_GOOP)) {
                val existingLayers = state.get(LayerThresholdSpreadingBlock.LAYERS)
                val possibleLayers = LayerThresholdSpreadingBlock.MAX_LAYERS - existingLayers
                val placeableLayers = min(possibleLayers, goopToPlace)
                remainingGoop -= placeableLayers
                results[pos] = placeableLayers
            } else {
                val placeableLayers = min(remainingGoop, LayerThresholdSpreadingBlock.MAX_LAYERS)
                remainingGoop -= placeableLayers
                results[pos] = placeableLayers
            }
            addIfNotVisited(pos.down(), locationQueue, results)
            addIfNotVisited(pos.north(), locationQueue, results)
            addIfNotVisited(pos.south(), locationQueue, results)
            addIfNotVisited(pos.west(), locationQueue, results)
            addIfNotVisited(pos.east(), locationQueue, results)
            addIfNotVisited(pos.up(), locationQueue, results)
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