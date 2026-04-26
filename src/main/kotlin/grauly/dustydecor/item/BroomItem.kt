package grauly.dustydecor.item

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.layered.LayerThresholdSpreadingBlock
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.phys.AABB

class BroomItem(properties: Properties) : Item(properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.hand == InteractionHand.MAIN_HAND) {
            if (context.player?.isShiftKeyDown == true) {
                context.player?.sendOverlayMessage(Component.literal("Found ${sumFalling(context)} falling layers"))
            } else {
                context.player?.sendOverlayMessage(Component.literal("Found ${sumResting(context)} resting layers"))
            }
        } else {
            context.player?.sendOverlayMessage(
                Component.literal("Found ${sumFalling(context) + sumResting(context)} total layers")
            )
        }
        return InteractionResult.SUCCESS
    }

    fun sumFalling(context: UseOnContext): Int {
        val range = 5.0
        val falling = context.level.getEntities(
            context.player,
            AABB(context.clickedPos).inflate(range),
            { entity -> entity is FallingBlockEntity }
        )
        val layers = falling
            .map { it as FallingBlockEntity }
            .filter { it.blockState.`is`(ModBlocks.VOID_GOOP) }
            .sumOf { it.blockState.getValue(LayerThresholdSpreadingBlock.LAYERS) }
        return layers
    }

    fun sumResting(context: UseOnContext): Int {
        val visited = mutableSetOf<BlockPos>()
        val found = mutableSetOf<BlockPos>()
        val range = 5
        for (x in (context.clickedPos.x - range)..(context.clickedPos.x + range)) {
            for (y in (context.clickedPos.y - range)..(context.clickedPos.y + range)) {
                for (z in (context.clickedPos.z - range)..(context.clickedPos.z + range)) {
                    val pos = BlockPos(x, y, z)
                    if (visited.contains(pos)) continue
                    (ModItems.OUTSIDE_CRYSTAL_SHARD as OutsideCrystalShardItem).getConnectedGoop(
                        BlockPos(x, y, z),
                        context.level,
                        found
                    )
                    visited.addAll(found)
                }
            }
        }
        val layers = found.sumOf { context.level.getBlockState(it).getValue(LayerThresholdSpreadingBlock.LAYERS) }
        return layers
    }
}