package grauly.dustydecor.block

import grauly.dustydecor.ModBlocks
import net.minecraft.block.Block
import net.minecraft.block.BlockSetType
import net.minecraft.block.BlockState
import net.minecraft.block.TrapdoorBlock
import net.minecraft.block.enums.BlockHalf
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random

class VentCoverBlock(settings: Settings): TrapdoorBlock(BlockSetType.COPPER, settings) {
    override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        val isConnectedToVentSideways = world.getBlockState(pos.offset(state.get(FACING).opposite)).isOf(ModBlocks.VENT)
        val isConnectedToVentAbove = world.getBlockState(pos.offset(Direction.UP)).isOf(ModBlocks.VENT)
        val isConnectedToVentBelow = world.getBlockState(pos.offset(Direction.DOWN)).isOf(ModBlocks.VENT)
        if (isConnectedToVentSideways && (isConnectedToVentAbove || isConnectedToVentBelow)) return //Cannot determine target state reliably
        if (isConnectedToVentSideways && state.get(HALF).equals(BlockHalf.TOP) && !state.get(OPEN)) {
            world.setBlockState(pos, state.with(OPEN, true), Block.NOTIFY_LISTENERS)
            this.playToggleSound(null, world, pos, true)
        }
        if (isConnectedToVentAbove && state.get(HALF).equals(BlockHalf.TOP) && state.get(OPEN)) {
            world.setBlockState(pos, state.with(OPEN, false), Block.NOTIFY_LISTENERS)
            this.playToggleSound(null, world, pos, false)
        }
        if (isConnectedToVentBelow && state.get(HALF).equals(BlockHalf.BOTTOM) && state.get(OPEN)) {
            world.setBlockState(pos, state.with(OPEN, false), Block.NOTIFY_LISTENERS)
            this.playToggleSound(null, world, pos, false)
        }
    }

    override fun hasRandomTicks(state: BlockState): Boolean {
        return super.hasRandomTicks(state) && !state.get(POWERED, false)
    }
}