package grauly.dustydecor.block.furniture

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.entity.SeatEntity
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

abstract class SittableFurnitureBlock(settings: Settings) : SingleFurnitureBlock(settings), SeatLinkable {
    abstract fun getSitOffset(): Vec3d

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult? {
        if (player.isSneaking) return ActionResult.SUCCESS_SERVER
        if (SeatEntity.createLinked(world, pos, getSitOffset(), player) == null) {
            player.sendMessage(Text.translatable(SEAT_OCCUPIED_TRANSLATION_KEY), true)
            return super.onUse(state, world, pos, player, hit)
        }
        return ActionResult.SUCCESS
    }

    companion object {
        const val SEAT_OCCUPIED_TRANSLATION_KEY = "sittable.${DustyDecorMod.MODID}.occupied"
    }
}