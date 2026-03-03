package grauly.dustydecor.block.furniture

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.furniture.SingleFurnitureBlock.Companion.ROTATION
import grauly.dustydecor.util.GlassUtils
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult

class ConnectingGlassTableFrameBlock(properties: Properties) : ConnectingBreakableBlock(properties) {
    override fun useItemOn(
        itemStack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (GlassUtils.GLASS_PANE_ORDER.map { it.asItem() }.contains(itemStack.item)) {
            val block = ModBlocks.CONNECTING_GLASS_TABLES[GlassUtils.GLASS_PANE_ORDER.map { it.asItem() }.indexOf(itemStack.item)]
            val replaceState = replaceBlock(block, level, state, pos)
            itemStack.consume(1, player)
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
            level.playSound(
                null,
                pos,
                replaceState.soundType.placeSound,
                SoundSource.BLOCKS
            )
            return InteractionResult.SUCCESS
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult)
    }

    override fun onProjectileHit(level: Level, state: BlockState, blockHit: BlockHitResult, projectile: Projectile) {
        //[Space intentionally left blank]
    }

    override fun attack(state: BlockState, level: Level, pos: BlockPos, player: Player) {
        //[Space intentionally left blank]
    }
}