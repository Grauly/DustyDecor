package grauly.dustydecor.block

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.util.ToolUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

class VacPipeBlock(settings: Settings) : AbConnectableBlock(settings) {

    init {
        windowStates.forEach {
            defaultState = defaultState.with(it, false)
        }
        defaultState = defaultState.with(SHOULD_HAVE_WINDOW, false)
        for (state: BlockState in stateManager.states) {
            val normalizedState = normalizeStateForShape(state)
            if (SHAPES.containsKey(normalizedState)) continue
            var workingShape = CORE_SHAPE
            connections.forEach {
                val connectionDirection = normalizedState.get(it)
                if (connectionDirection != ConnectionState.NONE) {
                    workingShape = VoxelShapes.union(workingShape, CONNECTOR_SHAPE_MAP[connectionDirection.direction])
                }
            }
            SHAPES[normalizedState] = workingShape
        }
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext?
    ): VoxelShape {
        return SHAPES[normalizeStateForShape(state)]!!
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext?
    ): VoxelShape {
        return SHAPES[normalizeStateForShape(state)]!!
    }

    private fun normalizeStateForShape(state: BlockState): BlockState {
        var workingState = defaultState
        connections.forEach { workingState = workingState.with(it, state.get(it)) }
        return workingState
    }

    override fun isConnectable(
        state: BlockState,
        pos: BlockPos,
        world: WorldView,
        connectionDirection: Direction
    ): Boolean {
        return state.isOf(ModBlocks.VAC_PIPE)
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
            val newWindowState: Boolean = togglePipeWindow(state, pos, world)
            ToolUtils.playScrewdriverSound(world, pos, player)
            world.playSound(
                player,
                pos,
                if (newWindowState) ModSoundEvents.BLOCK_VAP_PIPE_ADD_WINDOW else ModSoundEvents.BLOCK_VAP_PIPE_REMOVE_WINDOW,
                SoundCategory.BLOCKS
            )
        } else if (ToolUtils.isWrench(stack)) {
            ToolUtils.playWrenchSound(world, pos, player)
            val boxExpansion = 0.01
            val relativePos = hit.pos.subtract(Vec3d.of(pos))
            val clickedConnection: EnumProperty<ConnectionState>? = connections.firstOrNull {
                val connectionDirection = state.get(it, ConnectionState.NONE)
                if (connectionDirection == ConnectionState.NONE) {
                    return@firstOrNull false
                }
                val boundingBox = CONNECTOR_SHAPE_MAP[connectionDirection.direction]!!.boundingBox.expand(boxExpansion)
                return@firstOrNull boundingBox.contains(relativePos)
            }
            if (clickedConnection != null) {
                val success = tryDisableConnection(pos, state, clickedConnection, world)
                if (success) {
                    return ActionResult.SUCCESS
                    //TODO: success sound
                }
                //TODO: some fail sound
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit)
    }

    private fun tryDisableConnection(
        pos: BlockPos,
        state: BlockState,
        connection: EnumProperty<ConnectionState>,
        world: World,
        canTraverse: Boolean = true
    ): Boolean {
        val connectionDirection = state.get(connection, ConnectionState.NONE)
        if (connectionDirection == ConnectionState.NONE) return false
        val usedConnections =
            connections.map { state.get(it) }.filter { it != ConnectionState.NONE }.map { it.direction!! }
        val foundConnection =
            findConnection(pos, world, connectionDirection.direction!!, *usedConnections.toTypedArray())
        if (foundConnection == ConnectionState.NONE) {
            if (!canTraverse) return false
            val offsetPos = pos.offset(connectionDirection.direction)
            val offsetState = world.getBlockState(offsetPos)
            val otherConnection =
                connections.firstOrNull { offsetState.get(it) != ConnectionState.NONE && offsetState.get(it).direction!!.opposite == connectionDirection.direction }
            if (otherConnection == null) return false
            return tryDisableConnection(offsetPos, offsetState, otherConnection, world, false)
        }
        world.setBlockState(pos, updateWindows(state.with(connection, foundConnection), world, pos), NOTIFY_LISTENERS)
        return true
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
        val superState = super.getStateForNeighborUpdate(
            state,
            world,
            tickView,
            pos,
            direction,
            neighborPos,
            neighborState,
            random
        )
        return updateWindows(superState, world, pos)
    }

    private fun updateWindows(
        state: BlockState,
        world: WorldView,
        pos: BlockPos
    ): BlockState {
        var workingState = state
        val shouldHaveWindow = state.get(SHOULD_HAVE_WINDOW, false)
        for (connection in connections) {
            if (shouldHaveWindow) {
                workingState = workingState.with(windowMap[connection], true)
                continue
            }
            val connectionDirection = state.get(connection, ConnectionState.NONE)
            if (connectionDirection == ConnectionState.NONE) {
                workingState = workingState.with(windowMap[connection], shouldHaveWindow)
                continue
            }
            val checkState = world.getBlockState(pos.offset(connectionDirection.direction))
            if (checkState.get(SHOULD_HAVE_WINDOW, false)) {
                workingState = workingState.with(windowMap[connection], true)
                continue
            }
            workingState = workingState.with(windowMap[connection], false)
        }
        return workingState
    }

    private fun togglePipeWindow(state: BlockState, pos: BlockPos, world: World): Boolean {
        val newState = !state.get(SHOULD_HAVE_WINDOW)
        world.setBlockState(pos, state.with(SHOULD_HAVE_WINDOW, newState), NOTIFY_LISTENERS)
        return newState
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(*windowStates.toTypedArray(), SHOULD_HAVE_WINDOW)
    }

    override fun canPathfindThrough(state: BlockState?, type: NavigationType?): Boolean = false

    companion object {
        val windowStates: List<BooleanProperty> = listOf("a_window", "b_window").map { BooleanProperty.of(it) }
        val windowMap: Map<EnumProperty<ConnectionState>, BooleanProperty> = mapOf(
            connections[0] to windowStates[0],
            connections[1] to windowStates[1]
        )
        val SHAPES: MutableMap<BlockState, VoxelShape> = mutableMapOf()
        val SHOULD_HAVE_WINDOW: BooleanProperty = BooleanProperty.of("should_have_window")
        val CONNECTOR_SHAPE_MAP: Map<Direction, VoxelShape> = VoxelShapes.createFacingShapeMap(
            VoxelShapes.cuboid(4.0 / 16, 4.0 / 16, 0.0, 12.0 / 16, 12.0 / 16, 4.0 / 16)
        )
        val CORE_SHAPE: VoxelShape = VoxelShapes.cuboid(4.0 / 16, 4.0 / 16, 4.0 / 16, 12.0 / 16, 12.0 / 16, 12.0 / 16)
    }
}