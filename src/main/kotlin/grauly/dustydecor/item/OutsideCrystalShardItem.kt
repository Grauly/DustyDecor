package grauly.dustydecor.item

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.voidgoop.LayerThresholdSpreadingBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Blocks

class OutsideCrystalShardItem(settings: Properties) : Item(settings) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val interactedState = context.level.getBlockState(context.clickedPos)
        if (!interactedState.`is`(ModBlocks.VOID_GOOP)) return super.useOn(context)
        if (context.level.isClientSide) return super.useOn(context)
        val world = context.level as ServerLevel
        val foundVoidGoop: MutableSet<BlockPos> = mutableSetOf()
        val foundLayers = getConnectedGoop(context.clickedPos, world, foundVoidGoop)
        if (context.player?.isShiftKeyDown == false) {
            context.player?.displayClientMessage(
                Component.translatable(
                    VOID_GOOP_FIND,
                    foundLayers,
                    ModItems.VOID_GOOP.getName(ModItems.VOID_GOOP.defaultInstance)
                ), true
            )
            return InteractionResult.SUCCESS
        }
        removeFoundGoop(foundVoidGoop, world)
        context.itemInHand.consume(1, context.player)
        context.player?.displayClientMessage(
            Component.translatable(
                VOID_GOOP_REMOVAL,
                foundLayers,
                ModItems.VOID_GOOP.getName(ModItems.VOID_GOOP.defaultInstance)
            ), true
        )
        return InteractionResult.SUCCESS
    }

    private fun removeFoundGoop(foundVoidGoop: MutableSet<BlockPos>, world: ServerLevel) {
        foundVoidGoop.forEach {
            world.setBlockAndUpdate(it, Blocks.AIR.defaultBlockState())
        }
    }

    fun getConnectedGoop(pos: BlockPos, world: LevelAccessor, found: MutableSet<BlockPos>): Int {
        if (found.contains(pos)) return 0
        val state = world.getBlockState(pos)
        if (!state.`is`(ModBlocks.VOID_GOOP)) return 0
        found.add(pos)
        var foundLayers = state.getValue(LayerThresholdSpreadingBlock.LAYERS)
        Direction.entries.forEach {
            foundLayers += getConnectedGoop(pos.relative(it), world, found)
        }
        return foundLayers
    }

    companion object {
        const val VOID_GOOP_REMOVAL: String = "item.void_sponge.usage"
        const val VOID_GOOP_FIND: String = "item.void_sponge.measure"
    }

}