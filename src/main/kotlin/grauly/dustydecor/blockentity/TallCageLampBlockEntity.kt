package grauly.dustydecor.blockentity

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.LightingFixtureBlock
import grauly.dustydecor.util.DyeUtils
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.random.Random

class TallCageLampBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(
    ModBlockEntityTypes.TALL_CAGE_LAMP_ENTITY,
    pos,
    state
) {
    var age: Int = 0
    var color: Int = 0

    init {
        age += RANDOM.nextInt(20)
    }

    fun shouldShowBeams(): Boolean {
        if (cachedState.get(LightingFixtureBlock.BROKEN)) return false
        return cachedState.get(LightingFixtureBlock.LIT) != cachedState.get(LightingFixtureBlock.INVERTED)
    }

    fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: TallCageLampBlockEntity) {
        //I hate how tickDelta (or now tickProgress) works. I would not have to tick this if it just worked normally
        age++
        if (color == 0) {
            color = DyeUtils.COLOR_ORDER[ModBlocks.TALL_CAGE_LAMPS.indexOf(world.getBlockState(pos).block)].signColor
        }
    }

    companion object {
        private val RANDOM = Random.Default
    }
}