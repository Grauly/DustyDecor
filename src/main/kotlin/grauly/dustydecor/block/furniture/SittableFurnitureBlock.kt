package grauly.dustydecor.block.furniture

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.entity.SeatEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.entity.player.Player
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level

abstract class SittableFurnitureBlock(settings: Properties) : SingleFurnitureBlock(settings), SeatLinkable {
    abstract fun getSitOffset(): Vec3

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult? {
        if (player.isShiftKeyDown) return InteractionResult.SUCCESS_SERVER
        if (SeatEntity.createLinked(world, pos, getSitOffset(), player) == null) {
            player.displayClientMessage(Component.translatable(SEAT_OCCUPIED_TRANSLATION_KEY), true)
            return super.useWithoutItem(state, world, pos, player, hit)
        }
        return InteractionResult.SUCCESS
    }

    companion object {
        const val SEAT_OCCUPIED_TRANSLATION_KEY = "sittable.${DustyDecorMod.MODID}.occupied"
    }
}