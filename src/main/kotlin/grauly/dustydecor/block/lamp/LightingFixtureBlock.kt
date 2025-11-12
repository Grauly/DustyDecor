package grauly.dustydecor.block.lamp

import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.extensions.spawnParticle
import grauly.dustydecor.particle.SparkEmitterParticleEffect
import grauly.dustydecor.util.ToolUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Waterloggable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.block.WireOrientation
import net.minecraft.world.tick.ScheduledTickView

abstract class LightingFixtureBlock(settings: Settings?) : Block(settings), Waterloggable {

    init {
        defaultState = defaultState
            .with(LIT, false)
            .with(INVERTED, false)
            .with(BROKEN, false)
            .with(Properties.WATERLOGGED, false)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return defaultState
            .with(LIT, ctx.world.isReceivingRedstonePower(ctx.blockPos))
            .with(Properties.WATERLOGGED, ctx.world.getFluidState(ctx.blockPos).fluid == Fluids.WATER)
    }

    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        sourceBlock: Block,
        wireOrientation: WireOrientation?,
        notify: Boolean
    ) {
        if (world.isClient) return
        val isPowered = world.isReceivingRedstonePower(pos)
        if (state.get(LIT) == isPowered) return
        changeOnState(isPowered, state, pos, world)
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
        if (ToolUtils.isScrewdriver(stack)) {
            ToolUtils.playScrewdriverSound(world, pos, player)
            toggleInverted(state, pos, world, player)
            return ActionResult.SUCCESS
        }
        if (ToolUtils.isWrench(stack)) {
            val hasRepaired = repair(state, pos, world, player)
            if (hasRepaired) {
                ToolUtils.playWrenchSound(world, pos, player)
                return ActionResult.SUCCESS
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit)
    }

    override fun onProjectileHit(
        world: World,
        state: BlockState,
        hit: BlockHitResult,
        projectile: ProjectileEntity
    ) {
        breakFixture(state, hit.blockPos, world)
        super.onProjectileHit(world, state, hit, projectile)
    }

    override fun onBlockBreakStart(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity) {
        val stack = player.getStackInHand(Hand.MAIN_HAND)
        if (ToolUtils.isWrench(stack)) {
            breakFixture(state, pos, world)
        }
        super.onBlockBreakStart(state, world, pos, player)
    }

    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (!state.get(BROKEN)) return
        if (random.nextInt(40) != 1) return
        spark(state, pos, world)
    }

    override fun hasRandomTicks(state: BlockState): Boolean {
        return super.hasRandomTicks(state) && state.get(BROKEN)
    }

    protected open fun changeOnState(shouldBeOn: Boolean, state: BlockState, pos: BlockPos, world: World): Boolean {
        val isLit = state.get(LIT)
        val isBroken = state.get(BROKEN)
        if (isBroken) { //Broken lamps can turn off, but no longer on
            spark(state, pos, world)
            if (!shouldBeOn && isLit) {
                world.setBlockState(pos, state.with(LIT, false))
                return true
            }
            return false
        }
        if (isLit == shouldBeOn) return false
        world.playSound(
            null,
            pos,
            if (shouldBeOn) ModSoundEvents.BLOCK_LIGHTING_FIXTURE_TURN_ON else ModSoundEvents.BLOCK_LIGHTING_FIXTURE_TURN_OFF,
            SoundCategory.BLOCKS
        )
        world.setBlockState(pos, state.with(LIT, shouldBeOn))
        return true
    }

    protected open fun spark(state: BlockState, pos: BlockPos, world: World) {
        //TODO: implement sounds
        if (world !is ServerWorld) return
        val sparkEffect = SparkEmitterParticleEffect(0.1, 12, true)
        val sparkDirection = state.get(Properties.FACING)
        world.spawnParticle(sparkEffect, pos.toCenterPos(), sparkDirection.doubleVector, 0.4)
    }

    protected open fun breakFixture(state: BlockState, pos: BlockPos, world: World): Boolean {
        val isBroken = state.get(BROKEN)
        if (isBroken) return false
        world.playSound(null, pos, ModSoundEvents.BLOCK_LIGHTING_FIXTURE_BREAK, SoundCategory.BLOCKS)
        spark(state, pos, world)
        world.setBlockState(pos, state.with(BROKEN, true), NOTIFY_LISTENERS)
        return true
    }

    protected open fun repair(state: BlockState, pos: BlockPos, world: World, player: PlayerEntity): Boolean {
        val isBroken = state.get(BROKEN)
        if (!isBroken) return false
        world.playSound(
            player,
            pos,
            ModSoundEvents.BLOCK_LIGHTING_FIXTURE_REPAIR,
            SoundCategory.BLOCKS
        )
        world.setBlockState(pos, state.with(BROKEN, false), NOTIFY_LISTENERS)
        return true
    }

    protected open fun toggleInverted(state: BlockState, pos: BlockPos, world: World, player: PlayerEntity): Boolean {
        world.playSound(
            player,
            pos,
            ModSoundEvents.BLOCK_LIGHTING_FIXTURE_INVERT,
            SoundCategory.BLOCKS
        )
        world.setBlockState(pos, state.with(INVERTED, !state.get(INVERTED)))
        return true
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.get(Properties.WATERLOGGED)) {
            Fluids.WATER.getStill(false)
        } else {
            super.getFluidState(state)
        }
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: Random
    ): BlockState {
        if (state.get(Properties.WATERLOGGED)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }
        return super.getStateForNeighborUpdate(
            state,
            world,
            tickView,
            pos,
            direction,
            neighborPos,
            neighborState,
            random
        )
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(LIT, INVERTED, BROKEN, Properties.WATERLOGGED)
    }

    abstract override fun mirror(state: BlockState, mirror: BlockMirror): BlockState
    abstract override fun rotate(state: BlockState, rotation: BlockRotation): BlockState

    companion object {
        val LIT: BooleanProperty = BooleanProperty.of("on")
        val INVERTED: BooleanProperty = BooleanProperty.of("inverted")
        val BROKEN: BooleanProperty = BooleanProperty.of("broken")
        fun getLightingFunction(broken: Int, active: Int): (BlockState) -> Int {
            return lambda@{ state: BlockState ->
                if (state.get(LIT) == state.get(INVERTED)) return@lambda 0
                if (state.get(BROKEN)) return@lambda broken
                return@lambda active
            }
        }
    }
}