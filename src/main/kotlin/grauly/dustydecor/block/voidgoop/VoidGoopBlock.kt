package grauly.dustydecor.block.voidgoop

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModItemTags
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.util.ExtraCodecs
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockBehaviour
import java.awt.Color

class VoidGoopBlock(threshold: Int, settings: Properties) : LayerThresholdSpreadingBlock(threshold, settings) {

    //TODO: add gazing interaction (haha, player go splat)
    //TODO: add eye shaped rain splashes
    //TODO: add outsider spawning in large enough pools
    //TODO: add anti destruction mechanics: tp away
    //TODO: find a way to massively discourage just tp-ing it into random caves
    //TODO: fix the two-layer stable states from being permanent

    override fun canBeReplaced(
        state: BlockState,
        context: BlockPlaceContext
    ): Boolean {
        if (context.itemInHand.`is`(ModItemTags.VOID_GOOP)) {
            if (state.getValue(LAYERS) < MAX_LAYERS) {
                return true
            }
        }
        return super.canBeReplaced(state, context)
    }

    override fun codec(): MapCodec<out VoidGoopBlock> {
        return RecordCodecBuilder.mapCodec {
            it.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("threshold").forGetter { block -> block.threshold },
                propertiesCodec()
            ).apply(it, ::VoidGoopBlock)
        }
    }

    override fun getDustColor(state: BlockState, world: BlockGetter, pos: BlockPos): Int {
        return Color(0f, 0f, 0f, 1f).rgb
    }
}