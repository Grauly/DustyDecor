package grauly.dustydecor.block.furniture

import grauly.dustydecor.entity.SeatEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.entity.player.Player
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.pathfinder.PathComputationType

abstract class SittableFurnitureBlock(settings: Properties) : SingleFurnitureBlock(settings), SeatLinkable {
    abstract fun getSitOffset(state: BlockState): Vec3

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if (player.isShiftKeyDown) return InteractionResult.SUCCESS_SERVER
        val seatResult = SeatEntity.seatEntity(world, pos, getSitOffset(state), player)
        if (seatResult.type.shouldDisplayMessage) {
            player.displayClientMessage(Component.translatable(seatResult.type.messageTranslationKey), true)
        }
        return InteractionResult.SUCCESS
    }

    override fun isPathfindable(
        state: BlockState,
        type: PathComputationType
    ): Boolean = false

    override fun canSurvive(
        state: BlockState,
        level: LevelReader,
        pos: BlockPos
    ): Boolean {
        if (!super.canSurvive(state, level, pos)) return false
        val checkState = level.getBlockState(pos.relative(Direction.DOWN))
        return checkState.isFaceSturdy(level, pos, Direction.UP)
    }
}