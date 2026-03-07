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
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ConnectingGlassTableBlock(properties: Properties) : ConnectingBreakableBlock(properties) {
    var collisionShapes: MutableMap<BlockState, VoxelShape> = mutableMapOf()
    var outlineShapes: MutableMap<BlockState, VoxelShape> = mutableMapOf()

    init {
        collisionShapes = ConnectingGlassTableShapes.generateCollisionShapes(stateDefinition)
        collisionShapes.replaceAll { state, shape ->
            Shapes.or(shape, TABLE_TOP)
        }
        outlineShapes = ConnectingGlassTableShapes.generateOutlineShapes(stateDefinition)
        outlineShapes.replaceAll { state, shape ->
            Shapes.or(shape, TABLE_TOP)
        }
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
        if (itemStack.has(ModDataComponentTypes.LARGE_GLASS_TABLE_STRIP_PANE)) {
            if (level !is ServerLevel) return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult)
            ToolUtils.playToolSound(itemStack, pos, level, player)
            repair(level, state, pos)
            player.inventory.placeItemBackInInventory(
                getPaneState(state.block).block.asItem().defaultInstance.copyWithCount(
                    1
                )
            )
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

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return outlineShapes[ConnectingGlassTableShapes.normalizeState(state)] ?: Shapes.block()
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return collisionShapes[ConnectingGlassTableShapes.normalizeState(state)] ?: Shapes.block()
    }

    companion object {
        val TABLE_TOP = column(16.0, 15.0, 16.0)
    }
}