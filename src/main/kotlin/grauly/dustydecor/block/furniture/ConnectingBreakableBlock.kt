package grauly.dustydecor.block.furniture

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.ImpactBreakable
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

open class ConnectingBreakableBlock(properties: Properties) : GranularHorizontalConnectingBlock(properties),
    ImpactBreakable {

    override fun onProjectileHit(level: Level, state: BlockState, blockHit: BlockHitResult, projectile: Projectile) {
        super.onProjectileHit(level, state, blockHit, projectile)
        onProjectileImpact(level, state, blockHit, projectile)
    }

    override fun attack(state: BlockState, level: Level, pos: BlockPos, player: Player) {
        super.attack(state, level, pos, player)
        onAttacked(state, level, pos, player)
    }

    override fun canConnectTo(ownPos: BlockPos, connectingPos: BlockPos, level: LevelReader): Boolean {
        val offsetState = level.getBlockState(connectingPos)
        return offsetState.block is ConnectingBreakableBlock
    }

    override fun isBlockConverting(): Boolean = true

    protected open fun replaceBlock(block: Block, level: Level, state: BlockState, pos: BlockPos): BlockState {
        val newState = block.defaultBlockState()
            .setValue(NORTH, state.getValue(NORTH))
            .setValue(EAST, state.getValue(EAST))
            .setValue(SOUTH, state.getValue(SOUTH))
            .setValue(WEST, state.getValue(WEST))
            .setValue(NORTH_WEST, state.getValue(NORTH_WEST))
            .setValue(NORTH_EAST, state.getValue(NORTH_EAST))
            .setValue(SOUTH_WEST, state.getValue(SOUTH_WEST))
            .setValue(SOUTH_EAST, state.getValue(SOUTH_EAST))
            .setValue(WATERLOGGED, state.getValue(WATERLOGGED))

        level.setBlock(pos, newState, UPDATE_ALL)
        return newState
    }

    override fun onBroken(level: ServerLevel, state: BlockState, pos: BlockPos) {
        super.onBroken(level, state, pos)
        replaceBlock(ModBlocks.CONNECTING_GLASS_TABLE_FRAME, level, state, pos)
    }
}