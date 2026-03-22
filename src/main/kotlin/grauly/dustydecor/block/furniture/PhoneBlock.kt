package grauly.dustydecor.block.furniture

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModDataComponentTypes
import grauly.dustydecor.ModParticleTypes
import grauly.dustydecor.block.ImpactBreakable
import grauly.dustydecor.extensions.spawnParticle
import grauly.dustydecor.particle.SparkEmitterParticleEffect
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class PhoneBlock(settings: Properties) : SingleFurnitureBlock(settings), ImpactBreakable {

    override fun useItemOn(
        itemStack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (itemStack.has(ModDataComponentTypes.PHONE_REPAIR)) {
            if (level !is ServerLevel) return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult)
            repair(level, state, pos)
            return InteractionResult.SUCCESS
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult)
    }

    override fun attack(state: BlockState, level: Level, pos: BlockPos, player: Player) {
        super.attack(state, level, pos, player)
        onAttacked(state, level, pos, player)
    }

    override fun onProjectileHit(level: Level, state: BlockState, blockHit: BlockHitResult, projectile: Projectile) {
        super.onProjectileHit(level, state, blockHit, projectile)
        onProjectileImpact(level, state, blockHit, projectile)
    }

    override fun playBreakParticleEffect(level: ServerLevel, state: BlockState, pos: BlockPos) {
        level.spawnParticle(
            SparkEmitterParticleEffect(0.5, 12, true),
            pos.center,
            Direction.UP.unitVec3,
            0.3
        )
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(BROKEN)
    }

    companion object {
        val BROKEN = ImpactBreakable.BROKEN
        val SHAPE = column(8.0, 0.0, 4.0)
    }
}