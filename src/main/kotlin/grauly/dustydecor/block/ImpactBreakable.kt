package grauly.dustydecor.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.packet.ClientboundBlockBreakParticlePayload
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult

interface ImpactBreakable {

    fun onProjectileImpact(
        level: Level,
        state: BlockState,
        hit: BlockHitResult,
        projectile: Projectile
    ) {
        if (level !is ServerLevel) return
        val pos = hit.blockPos
        if (!projectile.mayBreak(level)) return
        if (!projectile.mayInteract(level, pos)) return
        if (!canBeBrokenByProjectile(level, state, hit, projectile)) return
        breakByImpact(level, state, pos)
    }

    fun onAttacked(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player
    ) {
        if (level !is ServerLevel) return
        val stack = player.getItemInHand(InteractionHand.MAIN_HAND)
        if (stack.has(DataComponents.WEAPON) || stack.has(DataComponents.KINETIC_WEAPON)) {
            if (!stack.`is`(ConventionalItemTags.MELEE_WEAPON_TOOLS)) return
            if (!canBeBrokenByAttack(state, level, pos, player)) return
            breakByImpact(level, state, pos)
        }
    }

    fun canBeBrokenByProjectile(
        level: ServerLevel,
        state: BlockState,
        hit: BlockHitResult,
        projectile: Projectile
    ): Boolean {
        return true
    }

    fun canBeBrokenByAttack(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player
    ): Boolean {
        return true
    }

    fun breakByImpact(
        level: ServerLevel,
        state: BlockState,
        pos: BlockPos,
    ): Boolean {
        if (isBlockConverting()) {
            onBroken(level, state, pos)
            return true
        }
        if (state.hasProperty(BROKEN) && !state.getValue(BROKEN)) {
            level.setBlock(pos, state.setValue(BROKEN, true), Block.UPDATE_ALL)
            onBroken(level, state, pos)
            return true
        }
        return false
    }

    fun repair(
        level: ServerLevel,
        state: BlockState,
        pos: BlockPos,
    ): Boolean {
        if (isBlockConverting()) {
            onRepair(level, state, pos)
            return true
        }
        if (state.hasProperty(BROKEN) && state.getValue(BROKEN)) {
            level.setBlock(pos, state.setValue(BROKEN, false), Block.UPDATE_ALL)
            onRepair(level, state, pos)
            return true
        }
        return false
    }

    fun isBlockConverting(): Boolean = false

    fun getParticleEffectMultiplier(): Int = 1

    fun playBreakParticleEffect(
        level: ServerLevel,
        state: BlockState,
        pos: BlockPos,
    ) {
        val trackingPlayers = level.getPlayers { player -> player.chunkTrackingView.contains(level.getChunkAt(pos).pos) }
        trackingPlayers.forEach { player ->
            ServerPlayNetworking.send(player, ClientboundBlockBreakParticlePayload(pos, state))
        }
    }

    fun onBroken(
        level: ServerLevel,
        state: BlockState,
        pos: BlockPos,
    ) {
        level.playSound(
            null,
            pos,
            state.soundType.breakSound,
            SoundSource.BLOCKS
        )
        for (i in 0..getParticleEffectMultiplier()) {
            playBreakParticleEffect(level, state, pos)
        }
    }

    fun onRepair(
        level: ServerLevel,
        state: BlockState,
        pos: BlockPos,
    ) {

    }

    companion object {
        val BROKEN = BooleanProperty.create("broken")
    }
}