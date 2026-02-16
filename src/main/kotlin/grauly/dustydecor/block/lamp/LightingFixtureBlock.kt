package grauly.dustydecor.block.lamp

import grauly.dustydecor.ModDataComponentTypes
import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.extensions.spawnParticle
import grauly.dustydecor.particle.SparkEmitterParticleEffect
import grauly.dustydecor.util.ToolUtils
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.InteractionResult
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.redstone.Orientation
import net.minecraft.world.level.ScheduledTickAccess

abstract class LightingFixtureBlock(settings: Properties) : Block(settings), SimpleWaterloggedBlock {

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(LIT, false)
                .setValue(INVERTED, false)
                .setValue(BROKEN, false)
                .setValue(BlockStateProperties.WATERLOGGED, false)
        )
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(LIT, ctx.level.hasNeighborSignal(ctx.clickedPos))
            .setValue(BlockStateProperties.WATERLOGGED, ctx.level.getFluidState(ctx.clickedPos).type == Fluids.WATER)
    }

    override fun neighborChanged(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        sourceBlock: Block,
        wireOrientation: Orientation?,
        notify: Boolean
    ) {
        if (world.isClientSide) return
        val isPowered = world.hasNeighborSignal(pos)
        if (state.getValue(LIT) == isPowered) return
        changeOnState(isPowered, state, pos, world)
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (stack.has(ModDataComponentTypes.LAMPS_INVERT)) {
            ToolUtils.playToolSound(stack, pos, level, player)
            toggleInverted(state, pos, level, player)
            return InteractionResult.SUCCESS
        }
        if (stack.has(ModDataComponentTypes.LAMPS_REPAIR)) {
            val hasRepaired = repair(state, pos, level, player)
            if (hasRepaired) {
                ToolUtils.playToolSound(stack, pos, level, player)
                return InteractionResult.SUCCESS
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit)
    }

    override fun onProjectileHit(
        world: Level,
        state: BlockState,
        hit: BlockHitResult,
        projectile: Projectile
    ) {
        breakFixture(state, hit.blockPos, world)
        super.onProjectileHit(world, state, hit, projectile)
    }

    override fun attack(state: BlockState, world: Level, pos: BlockPos, player: Player) {
        val stack = player.getItemInHand(InteractionHand.MAIN_HAND)
        if (stack.has(DataComponents.WEAPON) || stack.has(DataComponents.KINETIC_WEAPON)) {
            breakFixture(state, pos, world)
        }
        super.attack(state, world, pos, player)
    }

    override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (!state.getValue(BROKEN)) return
        if (random.nextInt(40) != 1) return
        spark(state, pos, world)
    }

    override fun isRandomlyTicking(state: BlockState): Boolean {
        return super.isRandomlyTicking(state) && state.getValue(BROKEN)
    }

    protected open fun changeOnState(shouldBeOn: Boolean, state: BlockState, pos: BlockPos, world: Level): Boolean {
        val isLit = state.getValue(LIT)
        val isBroken = state.getValue(BROKEN)
        if (isBroken) { //Broken lamps can turn off, but no longer on
            spark(state, pos, world)
            if (!shouldBeOn && isLit) {
                world.setBlockAndUpdate(pos, state.setValue(LIT, false))
                return true
            }
            return false
        }
        if (isLit == shouldBeOn) return false
        world.playSound(
            null,
            pos,
            if (shouldBeOn) ModSoundEvents.BLOCK_LIGHTING_FIXTURE_TURN_ON else ModSoundEvents.BLOCK_LIGHTING_FIXTURE_TURN_OFF,
            SoundSource.BLOCKS
        )
        world.setBlockAndUpdate(pos, state.setValue(LIT, shouldBeOn))
        return true
    }

    protected open fun spark(state: BlockState, pos: BlockPos, world: Level) {
        //TODO: implement sounds
        if (world !is ServerLevel) return
        val sparkEffect = SparkEmitterParticleEffect(0.1, 12, true)
        val sparkDirection = state.getValue(BlockStateProperties.FACING)
        world.spawnParticle(sparkEffect, pos.center, sparkDirection.unitVec3, 0.4)
    }

    protected open fun breakFixture(state: BlockState, pos: BlockPos, world: Level): Boolean {
        val isBroken = state.getValue(BROKEN)
        if (isBroken) return false
        world.playSound(null, pos, ModSoundEvents.BLOCK_LIGHTING_FIXTURE_BREAK, SoundSource.BLOCKS)
        spark(state, pos, world)
        world.setBlock(pos, state.setValue(BROKEN, true), UPDATE_CLIENTS)
        return true
    }

    protected open fun repair(state: BlockState, pos: BlockPos, world: Level, player: Player): Boolean {
        val isBroken = state.getValue(BROKEN)
        if (!isBroken) return false
        world.playSound(
            player,
            pos,
            ModSoundEvents.BLOCK_LIGHTING_FIXTURE_REPAIR,
            SoundSource.BLOCKS
        )
        world.setBlock(pos, state.setValue(BROKEN, false), UPDATE_CLIENTS)
        return true
    }

    protected open fun toggleInverted(state: BlockState, pos: BlockPos, world: Level, player: Player): Boolean {
        world.playSound(
            player,
            pos,
            ModSoundEvents.BLOCK_LIGHTING_FIXTURE_INVERT,
            SoundSource.BLOCKS
        )
        world.setBlockAndUpdate(pos, state.setValue(INVERTED, !state.getValue(INVERTED)))
        return true
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            Fluids.WATER.getSource(false)
        } else {
            super.getFluidState(state)
        }
    }

    override fun updateShape(
        state: BlockState,
        world: LevelReader,
        tickView: ScheduledTickAccess,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: RandomSource
    ): BlockState {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))
        }
        return super.updateShape(
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

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(LIT, INVERTED, BROKEN, BlockStateProperties.WATERLOGGED)
    }

    abstract override fun mirror(state: BlockState, mirror: Mirror): BlockState
    abstract override fun rotate(state: BlockState, rotation: Rotation): BlockState

    companion object {
        val LIT: BooleanProperty = BooleanProperty.create("on")
        val INVERTED: BooleanProperty = BooleanProperty.create("inverted")
        val BROKEN: BooleanProperty = BooleanProperty.create("broken")
        fun getLightingFunction(broken: Int, active: Int): (BlockState) -> Int {
            return lambda@{ state: BlockState ->
                if (state.getValue(LIT) == state.getValue(INVERTED)) return@lambda 0
                if (state.getValue(BROKEN)) return@lambda broken
                return@lambda active
            }
        }
    }
}