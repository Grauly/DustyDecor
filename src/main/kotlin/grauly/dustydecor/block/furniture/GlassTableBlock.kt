package grauly.dustydecor.block.furniture

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.util.DyeUtils
import grauly.dustydecor.util.GlassUtils
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class GlassTableBlock(settings: Properties) : RestrictedRotationFurnitureBlock(settings) {
    override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return VISUAL_SHAPE
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return COLLISION_SHAPE
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return OUTLINE_SHAPE
    }

    override fun onProjectileHit(
        level: Level,
        state: BlockState,
        blockHit: BlockHitResult,
        projectile: Projectile
    ) {
        val replaceState = ModBlocks.SMALL_GLASS_TABLE_FRAME.defaultBlockState()
            .setValue(ROTATION, state.getValue(ROTATION))
            .setValue(WATERLOGGED, state.getValue(WATERLOGGED))
        level.setBlock(blockHit.blockPos, replaceState, UPDATE_ALL)
        level.playSound(
            null,
            blockHit.blockPos,
            getSoundType(state).breakSound,
            SoundSource.BLOCKS
        )
        blockHit.blockPos.bottomCenter.add(0.0, 15.5 / 16.0, 0.0)
        for (i in 0..5) {
            level.addDestroyBlockEffect(blockHit.blockPos, getGlassState(state.block))
        }
    }

    fun getGlassState(block: Block): BlockState {
        return GlassUtils.GLASS_PANE_ORDER[ModBlocks.SMALL_GLASS_TABLES.indexOf(block)].defaultBlockState()
    }

    companion object {
        val OUTLINE_SHAPE: VoxelShape = Shapes.block()
        val COLLISION_SHAPE: VoxelShape = column(16.0, 15.0, 16.0)
        val VISUAL_SHAPE: VoxelShape = Shapes.empty()
    }
}