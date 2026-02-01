package grauly.dustydecor.block.vent

import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.util.ToolUtils
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level

class VentCoverBlock(settings: Properties) : TrapDoorBlock(BlockSetType.COPPER, settings) {

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(BlockStateProperties.LOCKED, false)
                .setValue(COVERS_FACE, Direction.UP)
        )
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (isModifyTool(stack)) {
            ToolUtils.playScrewdriverSound(world, pos, player)
            world.setBlock(
                pos,
                state.setValue(BlockStateProperties.LOCKED, !state.getValue(BlockStateProperties.LOCKED)),
                UPDATE_CLIENTS
            )
            if (state.getValue(BlockStateProperties.LOCKED)) {
                playUnlockSound(world, player, pos)
            } else {
                playLockSound(world, player, pos)
            }
            return InteractionResult.SUCCESS
        }
        return super.useItemOn(stack, state, world, pos, player, hand, hit)
    }

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if (state.getValueOrElse(BlockStateProperties.LOCKED, false)) {
            playRattleSound(world, player, pos)
            return InteractionResult.SUCCESS
        }
        return super.useWithoutItem(state, world, pos, player, hit)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val directionPlacedAgainst = ctx.clickedFace
        val checkHand = if (ctx.hand == InteractionHand.MAIN_HAND) InteractionHand.OFF_HAND else InteractionHand.MAIN_HAND
        val isHoldingScrewdriver = isModifyTool(ctx.player?.getItemInHand(checkHand) ?: ItemStack.EMPTY)
        if (isHoldingScrewdriver) {
            ToolUtils.playScrewdriverSound(ctx.level, ctx.clickedPos, ctx.player)
            playLockSound(ctx.level, ctx.player, ctx.clickedPos)
        }
        return super.getStateForPlacement(ctx)!!
            .setValue(COVERS_FACE, directionPlacedAgainst.opposite)
            .setValue(OPEN, directionPlacedAgainst.axis.isHorizontal)
            .setValue(BlockStateProperties.LOCKED, isHoldingScrewdriver)
    }

    override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        //target state is open = true for horizontal covers, and open = false for vertical covers
        val isHorizontal = state.getValue(COVERS_FACE).axis.isHorizontal
        if (!state.getValue(OPEN) && isHorizontal || state.getValue(OPEN) && !isHorizontal) {
            world.setBlock(pos, state.setValue(OPEN, !state.getValue(OPEN)), UPDATE_CLIENTS)
            this.playSound(null, world, pos, true)
        }
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(BlockStateProperties.LOCKED, COVERS_FACE)
    }

    override fun isRandomlyTicking(state: BlockState): Boolean {
        return super.isRandomlyTicking(state) && !state.getValueOrElse(POWERED, false) && !state.getValue(
            BlockStateProperties.LOCKED)
    }

    private fun isModifyTool(stack: ItemStack): Boolean = ToolUtils.isScrewdriver(stack)

    private fun playLockSound(world: Level, player: Player?, pos: BlockPos) {
        world.playSound(
            player,
            pos,
            ModSoundEvents.BLOCK_VENT_LOCK,
            SoundSource.BLOCKS
        )
    }

    private fun playUnlockSound(world: Level, player: Player?, pos: BlockPos) {
        world.playSound(
            player,
            pos,
            ModSoundEvents.BLOCK_VENT_UNLOCK,
            SoundSource.BLOCKS
        )
    }

    private fun playRattleSound(world: Level, player: Player?, pos: BlockPos) {
        world.playSound(
            player,
            pos,
            ModSoundEvents.BLOCK_VENT_RATTLE,
            SoundSource.BLOCKS
        )
    }

    companion object {
        val COVERS_FACE: EnumProperty<Direction> = EnumProperty.create("covers", Direction::class.java)
    }
}