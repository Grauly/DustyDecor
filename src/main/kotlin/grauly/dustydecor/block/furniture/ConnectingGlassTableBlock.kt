package grauly.dustydecor.block.furniture

import grauly.dustydecor.block.ImpactBreakable
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult

class ConnectingGlassTableBlock(properties: Properties) : GranularHorizontalConnectingBlock(properties), ImpactBreakable {

    override fun onProjectileHit(level: Level, state: BlockState, blockHit: BlockHitResult, projectile: Projectile) {
        super.onProjectileHit(level, state, blockHit, projectile)
        onProjectileImpact(level, state, blockHit, projectile)
    }

    override fun attack(state: BlockState, level: Level, pos: BlockPos, player: Player) {
        super.attack(state, level, pos, player)
        onAttacked(state, level, pos, player)
    }
}