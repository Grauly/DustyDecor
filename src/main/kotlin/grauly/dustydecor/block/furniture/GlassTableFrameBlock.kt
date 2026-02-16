package grauly.dustydecor.block.furniture

import com.mojang.math.OctahedralGroup
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.util.GlassUtils
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class GlassTableFrameBlock(settings: Properties) : RestrictedRotationFurnitureBlock(settings) {

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
            val replaceState = ModBlocks.SMALL_GLASS_TABLES[GlassUtils.GLASS_PANE_ORDER.map { it.asItem() }
                .indexOf(itemStack.item)]
                .defaultBlockState()
                .setValue(ROTATION, state.getValue(ROTATION))
                .setValue(WATERLOGGED, state.getValue(WATERLOGGED))
            itemStack.consume(1, player)
            level.setBlock(pos, replaceState, UPDATE_ALL)
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

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return OUTLINE_SHAPE
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return COLLISION_SHAPE
    }

    override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return VISUAL_SHAPE
    }

    companion object {
        val OUTLINE_SHAPE: VoxelShape = column(14.0, 0.0, 15.0)
        val FRAME_COLLIDER: VoxelShape = box(1.0, 15.0, 1.0, 15.0, 16.0, 1.1)
        val COLLISION_SHAPE: VoxelShape = Shapes.or(
            FRAME_COLLIDER,
            Shapes.rotate(FRAME_COLLIDER, OctahedralGroup.BLOCK_ROT_Y_90, Vec3(.5, .5, .5)),
            Shapes.rotate(FRAME_COLLIDER, OctahedralGroup.BLOCK_ROT_Y_180, Vec3(.5, .5, .5)),
            Shapes.rotate(FRAME_COLLIDER, OctahedralGroup.BLOCK_ROT_Y_270, Vec3(.5, .5, .5))
        )
        val VISUAL_SHAPE: VoxelShape = Shapes.empty()
    }
}