package grauly.dustydecor.block

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModConventionalItemTags
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
        defaultState = defaultState.with(SHOULD_HAVE_WINDOW, false)
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
        if (stack.isIn(ModConventionalItemTags.SCREWDRIVER_TOOLS)) {
            togglePipeWindow(state, pos, world)
            //TODO play screwdriver sounds
        } else if (stack.isIn(ConventionalItemTags.WRENCH_TOOLS)) {
            //TODO: adjustment of pipe connections
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
        val shouldHaveWindow = state.get(SHOULD_HAVE_WINDOW, false)
        for (connection in connections) {
            if (shouldHaveWindow) {
                workingState = workingState.with(windowMap[connection], true)
                continue
            }
            val connectionDirection = state.get(connection, ConnectionState.NONE)
            if (connectionDirection == ConnectionState.NONE) continue
            val checkState = world.getBlockState(pos.offset(connectionDirection.direction))
            if (checkState.get(SHOULD_HAVE_WINDOW, false)) {
                workingState = workingState.with(windowMap[connection], true)
                continue
            }
            workingState = workingState.with(windowMap[connection], false)
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
        world.setBlockState(pos, state.with(SHOULD_HAVE_WINDOW, !state.get(SHOULD_HAVE_WINDOW, false)))
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(*windowStates.toTypedArray(), SHOULD_HAVE_WINDOW)
    }

    companion object {
        val windowStates: List<BooleanProperty> = listOf("a_window", "b_window").map { BooleanProperty.of(it) }
        val windowMap: Map<EnumProperty<ConnectionState>, BooleanProperty> = mapOf(
            connections[0] to windowStates[0],
            connections[1] to windowStates[1]
        )
        val SHOULD_HAVE_WINDOW: BooleanProperty = BooleanProperty.of("should_have_window")
    }
}