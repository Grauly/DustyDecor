package grauly.dustydecor.block

import grauly.dustydecor.ModBlocks
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

class VacPipeBlock(settings: Settings) : AbConnectableBlock(settings) {

    init {
        windowStates.forEach {
            defaultState = defaultState.with(it, false)
        }
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
        if (stack.isIn(ConventionalItemTags.WRENCH_TOOLS)) {
            if (player.isSneaking) {
                togglePipeWindow(state, pos, world)
            } else {
                //TODO: adjust pipe connections
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit)
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
        var workingState = state
        for (connection in connections) {
            val connectionState = state.get(connection)
            if (connectionState == ConnectionState.NONE) continue
            val checkState = world.getBlockState(pos.offset(connectionState.direction!!))
            if (isFullWindow(checkState)) {
                workingState = workingState.with(windowMap[connection], true)
            } else {
                workingState = workingState.with(windowMap[connection], false)
            }
        }
        return super.getStateForNeighborUpdate(
            workingState,
            world,
            tickView,
            pos,
            direction,
            neighborPos,
            neighborState,
            random
        )
    }

    private fun togglePipeWindow(state: BlockState, pos: BlockPos, world: World) {
        if (state.get(windowStates[0]) == state.get(windowStates[1])) {
            val targetState = !state.get(windowStates[0])
            world.setBlockState(pos, state.with(windowStates[0], targetState).with(windowStates[1], targetState), NOTIFY_LISTENERS)
        } else {
            world.setBlockState(pos, state.with(windowStates[0], true).with(windowStates[1], true), NOTIFY_LISTENERS)
        }
    }

    private fun isFullWindow(state: BlockState): Boolean {
        var isFullWindow = true
        for (window in windowStates) {
            isFullWindow = isFullWindow && state.get(window, false)
        }
        return isFullWindow
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(*windowStates.toTypedArray())
    }

    companion object {
        val windowStates: List<BooleanProperty> = listOf("a_window", "b_window").map { BooleanProperty.of(it) }
        val windowMap: Map<EnumProperty<ConnectionState>, BooleanProperty> = mapOf(
            connections[0] to windowStates[0],
            connections[1] to windowStates[1]
        )
    }
}