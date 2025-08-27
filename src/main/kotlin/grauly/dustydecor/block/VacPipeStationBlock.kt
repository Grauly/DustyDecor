package grauly.dustydecor.block

import com.mojang.serialization.MapCodec
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.blockentity.VacPipeBlockEntity
import grauly.dustydecor.blockentity.VacPipeStationBlockEntity
import grauly.dustydecor.screen.VacPipeStationScreenHandler
import grauly.dustydecor.util.ToolUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.Waterloggable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

class VacPipeStationBlock(settings: Settings?) : HorizontalFacingBlock(settings), Waterloggable, BlockEntityProvider {

    init {
        defaultState = defaultState
            .with(FACING, Direction.NORTH)
            .with(Properties.WATERLOGGED, false)
            .with(SENDING, false)
    }

    private fun alignConnectedPipeNetwork(state: BlockState, pos: BlockPos, world: World) {
        if (!world.getBlockState(pos.offset(Direction.UP)).isOf(ModBlocks.VAC_PIPE)) return
        val nextPos = pos.offset(Direction.UP)
        val nextState = world.getBlockState(nextPos)
        (ModBlocks.VAC_PIPE as VacPipeBlock).alignPipeNetwork(nextState, state, nextPos, pos, Direction.UP, world)
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS
        val be = world.getBlockEntity(pos)
        if (be !is VacPipeStationBlockEntity) return ActionResult.SUCCESS
        player.openHandledScreen(be)
        return ActionResult.SUCCESS
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
        if (ToolUtils.isWrench(stack)) {
            invertSending(state, pos, world)
            ToolUtils.playWrenchSound(world, pos, player)
            return ActionResult.SUCCESS
        }
        if (ToolUtils.isScrewdriver(stack)) {
            //TODO: make this trigger automatically
            player.sendMessage(Text.literal("aliginig"), true)
            alignConnectedPipeNetwork(state, pos, world)
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit)
    }

    private fun invertSending(state: BlockState, pos: BlockPos, world: World) {
        //TODO: add sounds
        world.setBlockState(pos, state.with(SENDING, !state.get(SENDING)), NOTIFY_LISTENERS)
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
        if (state.get(Properties.WATERLOGGED, false)) {
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

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return super.getPlacementState(ctx)
            ?.with(FACING, ctx.horizontalPlayerFacing.opposite)
            ?.with(Properties.WATERLOGGED, ctx.world.getFluidState(ctx.blockPos).fluid == Fluids.WATER)
    }

    override fun onStateReplaced(state: BlockState, world: ServerWorld, pos: BlockPos, moved: Boolean) {
        if (state.block != ModBlocks.VAC_PIPE) {
            val be = world.getBlockEntity(pos)
            if (be is VacPipeBlockEntity) {
                ItemScatterer.spawn(world, pos, be)
            }
        }
        super.onStateReplaced(state, world, pos, moved)
    }

    override fun getCodec(): MapCodec<out HorizontalFacingBlock> {
        return createCodec(::VacPipeStationBlock)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(FACING, Properties.WATERLOGGED, SENDING)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return VacPipeStationBlockEntity(pos, state)
    }

    companion object {
        val SENDING: BooleanProperty = BooleanProperty.of("sending")
    }
}