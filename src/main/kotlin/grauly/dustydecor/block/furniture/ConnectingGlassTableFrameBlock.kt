package grauly.dustydecor.block.furniture

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.util.GlassUtils
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ConnectingGlassTableFrameBlock(properties: Properties) : ConnectingBreakableBlock(properties) {
    var collisionShapes: MutableMap<BlockState, VoxelShape> = mutableMapOf()
    var outlineShapes: MutableMap<BlockState, VoxelShape> = mutableMapOf()

    init {
        collisionShapes = ConnectingGlassTableShapes.generateCollisionShapes(stateDefinition)
        outlineShapes = ConnectingGlassTableShapes.generateOutlineShapes(stateDefinition)
    }

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
            val block = ModBlocks.CONNECTING_GLASS_TABLES[GlassUtils.GLASS_PANE_ORDER.map { it.asItem() }
                .indexOf(itemStack.item)]
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

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return collisionShapes[ConnectingGlassTableShapes.normalizeState(state)] ?: Shapes.block()
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return outlineShapes[ConnectingGlassTableShapes.normalizeState(state)] ?: Shapes.block()
    }

    override fun onProjectileHit(level: Level, state: BlockState, blockHit: BlockHitResult, projectile: Projectile) {
        //[Space intentionally left blank]
    }

    override fun attack(state: BlockState, level: Level, pos: BlockPos, player: Player) {
        //[Space intentionally left blank]
    }
}