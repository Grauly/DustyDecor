package grauly.dustydecor.block

import com.ibm.icu.text.MessagePattern.Part
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.extensions.spawnParticle
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.SnowBlock
import net.minecraft.item.AutomaticItemPlacementContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldView

class LayerThresholdSpreadingBlock(private val threshold: Int, settings: Settings?) : SnowBlock(settings) {

    override fun randomTick(state: BlockState?, world: ServerWorld?, pos: BlockPos?, random: Random?) {
        //dont do anything.
    }

    override fun onBlockAdded(
        state: BlockState,
        world: World,
        pos: BlockPos,
        oldState: BlockState,
        notify: Boolean
    ) {
        super.onBlockAdded(state, world, pos, oldState, notify)
        if (world.isClient) return
        world as ServerWorld
        trySpread(pos, world, state)
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val placedOnState = world.getBlockState(pos.down())
        return Block.isFaceFullSquare(placedOnState.getCollisionShape(world, pos.down()), Direction.UP) ||
                placedOnState.isOf(this) && placedOnState.get(LAYERS) == MAX_LAYERS
    }

    override fun canReplace(state: BlockState, context: ItemPlacementContext): Boolean {
        if (context.stack.isOf(this.asItem())) {
            return (state.get(LAYERS) < MAX_LAYERS)
        }
        return false
    }

    private fun trySpread(pos: BlockPos, world: ServerWorld, state: BlockState) {
        val layers = state.get(LAYERS)
        val spreadTargets: Map<Direction, Int> = Direction.entries.filter { it.axis.isHorizontal }.map {
            val offset = pos.offset(it)
            val offsetState = world.getBlockState(offset)
            val canPlace = offsetState.canPlaceAt(world, offset)
            val canReplace = offsetState.canReplace(AutomaticItemPlacementContext(world, offset, it, this.asItem().defaultStack, it.opposite))
            if (canPlace && canReplace) {
                if (offsetState.isOf(this)) {
                    return@map it to offsetState.get(LAYERS)
                }
                return@map it to 0
            }
            return@map null
        }.filterNotNull().toMap()
        if (spreadTargets.isEmpty()) return
        var updatedLayerCount = layers
        val spreadActions: MutableMap<Direction, Int> = mutableMapOf()
        for (i in 1..layers) {
            val updatedSpreadTargets: Map<Direction, Int> = spreadTargets.map { entry -> entry.key to entry.value + (spreadActions[entry.key] ?: 0) }.toMap()
            val lowest = updatedSpreadTargets.values.min()
            val lowestSpreadTargets = spreadTargets.filterValues { it <= lowest }
            if (lowest < updatedLayerCount - threshold) {
                spreadActions.compute(lowestSpreadTargets.keys.random()) { _, l ->
                    if (l != null) l + 1 else 1
                }
                updatedLayerCount -= 1
            } else {
                break
            }
        }
        spreadActions.forEach { entry ->
            val offsetPos = pos.offset(entry.key)
            val offsetState = world.getBlockState(offsetPos)
            if (offsetState.isOf(this)) {
                world.setBlockState(offsetPos, offsetState.with(LAYERS, offsetState.get(LAYERS) + entry.value))
            } else {
                world.setBlockState(offsetPos, defaultState.with(LAYERS, entry.value))
            }
        }
        world.setBlockState(pos, state.with(LAYERS, updatedLayerCount))
    }
}