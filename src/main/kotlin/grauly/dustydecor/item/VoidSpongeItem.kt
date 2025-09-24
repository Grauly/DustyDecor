package grauly.dustydecor.item

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.LayerThresholdSpreadingBlock
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess

class VoidSpongeItem(settings: Settings) : Item(settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult? {
        val interactedState = context.world.getBlockState(context.blockPos)
        if (!interactedState.isOf(ModBlocks.VOID_GOOP)) return super.useOnBlock(context)
        if (context.world.isClient) return super.useOnBlock(context)
        val world = context.world as ServerWorld
        val foundVoidGoop: MutableSet<BlockPos> = mutableSetOf()
        val foundLayers = getConnectedGoop(context.blockPos, world, foundVoidGoop)
        if (context.player?.isSneaking == false) {
            context.player?.sendMessage(Text.translatable(VOID_GOOP_FIND, foundLayers), true)
            return ActionResult.SUCCESS
        }
        removeFoundGoop(foundVoidGoop, world)
        context.stack.decrementUnlessCreative(1, context.player)
        context.player?.sendMessage(Text.translatable(VOID_GOOP_REMOVAL, foundLayers), true)
        return ActionResult.SUCCESS
    }

    private fun removeFoundGoop(foundVoidGoop: MutableSet<BlockPos>, world: ServerWorld) {
        foundVoidGoop.forEach {
            world.setBlockState(it, Blocks.AIR.defaultState)
        }
    }

    fun getConnectedGoop(pos: BlockPos, world: WorldAccess, found: MutableSet<BlockPos>): Int {
        if (found.contains(pos)) return 0
        val state = world.getBlockState(pos)
        if (!state.isOf(ModBlocks.VOID_GOOP)) return 0
        found.add(pos)
        var foundLayers = state.get(LayerThresholdSpreadingBlock.LAYERS)
        Direction.entries.forEach {
            foundLayers += getConnectedGoop(pos.offset(it), world, found)
        }
        return foundLayers
    }

    companion object {
        const val VOID_GOOP_REMOVAL: String = "item.void_sponge.usage"
        const val VOID_GOOP_FIND: String = "item.void_sponge.measure"
    }

}