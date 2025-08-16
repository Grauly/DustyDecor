package grauly.dustydecor.block

import grauly.dustydecor.ModConventionalItemTags
import grauly.dustydecor.ModSoundEvents
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.block.Block
import net.minecraft.block.BlockSetType
import net.minecraft.block.BlockState
import net.minecraft.block.TrapdoorBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

class VentCoverBlock(settings: Settings) : TrapdoorBlock(BlockSetType.COPPER, settings) {

    init {
        defaultState = defaultState
            .with(Properties.LOCKED, false)
            .with(COVERS_FACE, Direction.UP)
    }

    override fun onUseWithItem(
        stack: ItemStack,
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (isModifyTool(stack)) {
            world.setBlockState(
                pos,
                state.with(Properties.LOCKED, !state.get(Properties.LOCKED)),
                Block.NOTIFY_LISTENERS
            )
            if (state.get(Properties.LOCKED)) {
                playUnlockSound(world, player, pos)
            } else {
                playLockSound(world, player, pos)
            }
            return ActionResult.SUCCESS
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit)
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (state.get(Properties.LOCKED, false)) {
            playRattleSound(world, player, pos)
            return ActionResult.SUCCESS
        }
        return super.onUse(state, world, pos, player, hit)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val directionPlacedAgainst = ctx.side
        val checkHand = if (ctx.hand == Hand.MAIN_HAND) Hand.OFF_HAND else Hand.MAIN_HAND
        val isHoldingScrewdriver = isModifyTool(ctx.player?.getStackInHand(checkHand) ?: ItemStack.EMPTY)
        if (isHoldingScrewdriver) {
            playLockSound(ctx.world, ctx.player, ctx.blockPos)
        }
        return super.getPlacementState(ctx)
            ?.with(COVERS_FACE, directionPlacedAgainst.opposite)
            ?.with(OPEN, directionPlacedAgainst.axis.isHorizontal)
            ?.with(Properties.LOCKED, isHoldingScrewdriver)
    }

    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        //target state is open = true for horizontal covers, and open = false for vertical covers
        val isHorizontal = state.get(COVERS_FACE).axis.isHorizontal
        if (!state.get(OPEN) && isHorizontal || state.get(OPEN) && !isHorizontal) {
            world.setBlockState(pos, state.with(OPEN, !state.get(OPEN)), Block.NOTIFY_LISTENERS)
            this.playToggleSound(null, world, pos, true)
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(Properties.LOCKED, COVERS_FACE)
    }

    override fun hasRandomTicks(state: BlockState): Boolean {
        return super.hasRandomTicks(state) && !state.get(POWERED, false) && !state.get(Properties.LOCKED)
    }

    private fun isModifyTool(stack: ItemStack): Boolean = stack.isIn(ModConventionalItemTags.SCREWDRIVER_TOOLS)

    private fun playLockSound(world: World, player: PlayerEntity?, pos: BlockPos) {
        world.playSound(
            player,
            pos,
            ModSoundEvents.BLOCK_VENT_LOCK,
            SoundCategory.BLOCKS
        )
    }

    private fun playUnlockSound(world: World, player: PlayerEntity?, pos: BlockPos) {
        world.playSound(
            player,
            pos,
            ModSoundEvents.BLOCK_VENT_UNLOCK,
            SoundCategory.BLOCKS
        )
    }

    private fun playRattleSound(world: World, player: PlayerEntity?, pos: BlockPos) {
        world.playSound(
            player,
            pos,
            ModSoundEvents.BLOCK_VENT_RATTLE,
            SoundCategory.BLOCKS
        )
    }

    companion object {
        val COVERS_FACE: EnumProperty<Direction> = EnumProperty.of("covers", Direction::class.java)
    }
}