package grauly.dustydecor.block.furniture

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModDataComponentTypes
import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.util.GlassUtils
import grauly.dustydecor.util.ToolUtils
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class ConnectingGlassTableBlock(properties: Properties) : ConnectingBreakableBlock(properties) {
    override fun useItemOn(
        itemStack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (itemStack.has(ModDataComponentTypes.LARGE_GLASS_TABLE_STRIP_PANE)) {
            if (level !is ServerLevel) return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult)
            ToolUtils.playToolSound(itemStack, pos, level, player)
            repair(level, state, pos)
            player.inventory.placeItemBackInInventory(getPaneState(state.block).block.asItem().defaultInstance.copyWithCount(1))
            return InteractionResult.SUCCESS
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult)
    }

    override fun onRepair(level: ServerLevel, state: BlockState, pos: BlockPos) {
        replaceBlock(ModBlocks.CONNECTING_GLASS_TABLE_FRAME, level, state, pos)
        level.playSound(
            null,
            pos,
            ModSoundEvents.BLOCK_VAP_PIPE_REMOVE_WINDOW,
            SoundSource.BLOCKS
        )
    }

    fun getPaneState(block: Block): BlockState {
        return GlassUtils.GLASS_PANE_ORDER[ModBlocks.CONNECTING_GLASS_TABLES.indexOf(block)].defaultBlockState()
    }
}