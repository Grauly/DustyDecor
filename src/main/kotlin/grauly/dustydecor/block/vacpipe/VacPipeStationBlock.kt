package grauly.dustydecor.block.vacpipe

import com.mojang.serialization.MapCodec
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModDataComponentTypes
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity
import grauly.dustydecor.particle.AirInflowParticleEffect
import grauly.dustydecor.particle.AirOutflowParticleEffect
import grauly.dustydecor.util.ToolUtils
import grauly.dustydecor.util.VoxelShapesUtil
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.redstone.Orientation
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.SupportType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext

class VacPipeStationBlock(settings: Properties) : HorizontalDirectionalBlock(settings), SimpleWaterloggedBlock,
    EntityBlock {

    //TODO: do the pipe alignment automatically (find a compromise to stay performant)
    //TODO: add copper golem behaviors

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(SENDING, false)
        )
    }

    override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPE
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPE
    }

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if (world.isClientSide) return InteractionResult.SUCCESS
        val be = world.getBlockEntity(pos)
        if (be !is VacPipeStationBlockEntity) return InteractionResult.SUCCESS
        player.openMenu(be)
        return InteractionResult.SUCCESS
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
        if (stack.has(ModDataComponentTypes.VAC_STATION_INVERT)) {
            invertSending(state, pos, level)
            ToolUtils.playToolSound(stack, pos, level, player)
            return InteractionResult.SUCCESS
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit)
    }

    private fun invertSending(state: BlockState, pos: BlockPos, world: Level) {
        //TODO: add sounds
        world.setBlock(pos, state.setValue(SENDING, !state.getValue(SENDING)), UPDATE_CLIENTS)
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
        if (state.getValueOrElse(BlockStateProperties.WATERLOGGED, false)) {
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

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        return super.getStateForPlacement(ctx)
            ?.setValue(FACING, ctx.horizontalDirection.opposite)
            ?.setValue(BlockStateProperties.WATERLOGGED, ctx.level.getFluidState(ctx.clickedPos).type == Fluids.WATER)
    }

    override fun neighborChanged(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        block: Block,
        wireOrientation: Orientation?,
        notify: Boolean
    ) {
        if (world.isClientSide) return
        if (!world.hasNeighborSignal(pos)) return
        world.scheduleTick(pos, this, 4)
    }

    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val be = world.getBlockEntity(pos)
        if (be !is VacPipeStationBlockEntity) return
        val offsetPos = pos.relative(Direction.UP)
        if (!world.getBlockState(offsetPos).`is`(ModBlocks.VAC_PIPE)) return
        val otherStorage = ItemStorage.SIDED.find(world, pos.relative(Direction.UP), Direction.DOWN) ?: return
        val ownStorage = be.storage
        StorageUtil.move(ownStorage, otherStorage, { true }, 1, null)
    }

    override fun animateTick(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        random: RandomSource
    ) {
        if (!world.isClientSide) return
        indicateSending(state, world, pos)
        indicateLeak(state, world, pos)
    }

    private fun indicateSending (state: BlockState, world: Level, pos: BlockPos) {
        val effect =
            if (state.getValue(SENDING)) AirInflowParticleEffect(Direction.UP) else AirOutflowParticleEffect(Direction.DOWN)
        val origin = pos.bottomCenter.add(0.0, 12.0 / 16.0, 0.0)
        for (i in 0..1) {
            world.addParticle(
                effect,
                origin.x, origin.y, origin.z,
                0.0, 0.0, 0.0
            )
        }
    }

    private fun indicateLeak(
        state: BlockState,
        world: Level,
        pos: BlockPos
    ) {
        val topState = world.getBlockState(pos.relative(Direction.UP))
        val notCanFlow = topState.isFaceSturdy(world, pos.relative(Direction.UP), Direction.DOWN, SupportType.CENTER)
        val sending = state.getValue(SENDING)
        if (notCanFlow) {
            if (!topState.`is`(ModBlocks.VAC_PIPE)) return
            if (sending && topState.getValue(AbConnectableBlock.connections[0]) == ConnectionState.DOWN) return
            if (!sending && topState.getValue(AbConnectableBlock.connections[1]) == ConnectionState.DOWN) return
            indicatePipeLeak(world, pos, Direction.UP, state.getValue(SENDING))
            return
        }
        val topEffect =
            if (sending) AirOutflowParticleEffect(Direction.UP) else AirInflowParticleEffect(Direction.DOWN)
        val topOrigin = pos.relative(Direction.UP).bottomCenter
        for (i in 0..1) {
            world.addParticle(
                topEffect,
                topOrigin.x, topOrigin.y, topOrigin.z,
                0.0, 0.0, 0.0
            )
        }
    }


    override fun codec(): MapCodec<VacPipeStationBlock> {
        return simpleCodec(::VacPipeStationBlock)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(FACING, BlockStateProperties.WATERLOGGED, SENDING)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return VacPipeStationBlockEntity(pos, state)
    }

    companion object {
        val SENDING: BooleanProperty = BooleanProperty.create("sending")
        val SHAPE: VoxelShape = Shapes.or(
            VoxelShapesUtil.intCube(3, 0, 3, 13, 2, 13), //base plate
            VoxelShapesUtil.intCube(5, 2, 5, 11, 3, 11),
            VoxelShapesUtil.intCube(3, 11, 3, 13, 13, 13), //top plate
            VoxelShapesUtil.intCube(4, 13, 4, 12, 16, 12), //pipe

            VoxelShapesUtil.intCube(4, 2, 4, 5, 11, 5),
            VoxelShapesUtil.intCube(11, 2, 11, 12, 11, 12),
            VoxelShapesUtil.intCube(4, 2, 11, 5, 11, 12),
            VoxelShapesUtil.intCube(11, 2, 4, 12, 11, 5),
        )

        fun indicatePipeLeak(world: Level, pos: BlockPos, leakDirection: Direction, outflow: Boolean) {
            val origin = pos.center.add(leakDirection.unitVec3.scale(.5))
            UPDATE_SHAPE_ORDER.filter { it != leakDirection && it != leakDirection.opposite }.forEach { direction ->
                val offset = origin.add(direction.unitVec3.scale(4.0/16.0))
                val effect =
                    if (outflow) AirOutflowParticleEffect(direction) else AirInflowParticleEffect(direction.opposite)
                world.addParticle(
                    effect,
                    offset.x, offset.y, offset.z,
                    0.0, 0.0, 0.0
                )
            }
        }
    }
}