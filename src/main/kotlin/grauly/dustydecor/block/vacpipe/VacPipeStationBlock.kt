package grauly.dustydecor.block.vacpipe

import com.mojang.serialization.MapCodec
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity
import grauly.dustydecor.particle.AirInflowParticleEffect
import grauly.dustydecor.particle.AirOutflowParticleEffect
import grauly.dustydecor.util.ToolUtils
import grauly.dustydecor.util.VoxelShapesUtil
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.block.WireOrientation
import net.minecraft.world.tick.ScheduledTickView

class VacPipeStationBlock(settings: Settings?) : HorizontalFacingBlock(settings), Waterloggable, BlockEntityProvider {

    //TODO: do the pipe alignment automatically (find a compromise to stay performant)
    //TODO: add copper golem behaviors

    init {
        defaultState = defaultState
            .with(FACING, Direction.NORTH)
            .with(Properties.WATERLOGGED, false)
            .with(SENDING, false)
    }

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return SHAPE
    }

    override fun getCollisionShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return SHAPE
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

    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        sourceBlock: Block,
        wireOrientation: WireOrientation?,
        notify: Boolean
    ) {
        if (world.isClient) return
        if (!world.isReceivingRedstonePower(pos)) return
        world.scheduleBlockTick(pos, this, 4)
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        val be = world.getBlockEntity(pos)
        if (be !is VacPipeStationBlockEntity) return
        val offsetPos = pos.offset(Direction.UP)
        if (!world.getBlockState(offsetPos).isOf(ModBlocks.VAC_PIPE)) return
        val otherStorage = ItemStorage.SIDED.find(world, pos.offset(Direction.UP), Direction.DOWN) ?: return
        val ownStorage = be.storage
        StorageUtil.move(ownStorage, otherStorage, { true }, 1, null)
    }

    override fun randomDisplayTick(
        state: BlockState,
        world: World,
        pos: BlockPos,
        random: Random
    ) {
        if (!world.isClient) return
        indicateSending(state, world, pos)
        indicateLeak(state, world, pos)
    }

    private fun indicateSending (state: BlockState, world: World, pos: BlockPos) {
        val effect =
            if (state.get(SENDING)) AirInflowParticleEffect(Direction.UP) else AirOutflowParticleEffect(Direction.DOWN)
        val origin = pos.toBottomCenterPos().add(0.0, 12.0 / 16.0, 0.0)
        for (i in 0..1) {
            world.addParticleClient(
                effect,
                origin.x, origin.y, origin.z,
                0.0, 0.0, 0.0
            )
        }
    }

    private fun indicateLeak(
        state: BlockState,
        world: World,
        pos: BlockPos
    ) {
        val topState = world.getBlockState(pos.offset(Direction.UP))
        val notCanFlow = topState.isSideSolid(world, pos.offset(Direction.UP), Direction.DOWN, SideShapeType.CENTER)
        if (notCanFlow) {
            if (!topState.isOf(ModBlocks.VAC_PIPE)) return
            if (topState.get(AbConnectableBlock.connections[0]) == ConnectionState.DOWN) return
            indicatePipeLeak(world, pos, Direction.UP, state.get(SENDING))
            return
        }
        val topEffect =
            if (state.get(SENDING)) AirOutflowParticleEffect(Direction.UP) else AirInflowParticleEffect(Direction.DOWN)
        val topOrigin = pos.offset(Direction.UP).toBottomCenterPos()
        for (i in 0..1) {
            world.addParticleClient(
                topEffect,
                topOrigin.x, topOrigin.y, topOrigin.z,
                0.0, 0.0, 0.0
            )
        }
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
        val SHAPE: VoxelShape = VoxelShapes.union(
            VoxelShapesUtil.intCube(3, 0, 3, 13, 2, 13), //base plate
            VoxelShapesUtil.intCube(5, 2, 5, 11, 3, 11),
            VoxelShapesUtil.intCube(3, 11, 3, 13, 13, 13), //top plate
            VoxelShapesUtil.intCube(4, 13, 4, 12, 16, 12), //pipe

            VoxelShapesUtil.intCube(4, 2, 4, 5, 11, 5),
            VoxelShapesUtil.intCube(11, 2, 11, 12, 11, 12),
            VoxelShapesUtil.intCube(4, 2, 11, 5, 11, 12),
            VoxelShapesUtil.intCube(11, 2, 4, 12, 11, 5),
        )

        fun indicatePipeLeak(world: World, pos: BlockPos, leakDirection: Direction, outflow: Boolean) {
            val origin = pos.toCenterPos().add(leakDirection.doubleVector.multiply(.5))
            DIRECTIONS.filter { it != leakDirection && it != leakDirection.opposite }.forEach { direction ->
                val offset = origin.add(direction.doubleVector.multiply(4.0/16.0))
                val effect =
                    if (outflow) AirOutflowParticleEffect(direction) else AirInflowParticleEffect(direction.opposite)
                world.addParticleClient(
                    effect,
                    offset.x, offset.y, offset.z,
                    0.0, 0.0, 0.0
                )
            }
        }
    }
}