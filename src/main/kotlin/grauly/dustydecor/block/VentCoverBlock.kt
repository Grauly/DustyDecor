package grauly.dustydecor.block

import net.minecraft.block.Block
import net.minecraft.block.BlockSetType
import net.minecraft.block.BlockState
import net.minecraft.block.TrapdoorBlock
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random

class VentCoverBlock(settings: Settings) : TrapdoorBlock(BlockSetType.COPPER, settings) {

    init {
        defaultState = defaultState
            .with(Properties.LOCKED, false)
            .with(COVERS_FACE, Direction.UP)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val directionPlacedAgainst = ctx.side
        return super.getPlacementState(ctx)
            ?.with(COVERS_FACE, directionPlacedAgainst.opposite)
            ?.with(OPEN, !directionPlacedAgainst.axis.isHorizontal)
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

    companion object {
        val COVERS_FACE: EnumProperty<Direction> = EnumProperty.of("covers", Direction::class.java)
    }
}