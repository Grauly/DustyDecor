package grauly.dustydecor.block.voidgoop

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModItemTags
import net.minecraft.block.BlockState
import net.minecraft.item.ItemPlacementContext
import net.minecraft.util.dynamic.Codecs
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import java.awt.Color

class VoidGoopBlock(threshold: Int, settings: Settings?) : LayerThresholdSpreadingBlock(threshold, settings) {

    //TODO: add gazing interaction (haha, player go splat)
    //TODO: add eye shaped rain splashes
    //TODO: add outsider spawning in large enough pools
    //TODO: add anti destruction mechanics: tp away
    //TODO: find a way to massively discourage just tp-ing it into random caves
    //TODO: fix the two-layer stable states from being permanent

    override fun canReplace(
        state: BlockState,
        context: ItemPlacementContext
    ): Boolean {
        if (context.stack.isIn(ModItemTags.VOID_GOOP)) {
            if (state.get(LAYERS) < MAX_LAYERS) {
                return true
            }
        }
        return super.canReplace(state, context)
    }

    override fun getCodec(): MapCodec<out VoidGoopBlock> {
        return RecordCodecBuilder.mapCodec {
            it.group(
                Codecs.POSITIVE_INT.fieldOf("threshold").forGetter { block -> block.threshold },
                createSettingsCodec()
            ).apply(it, ::VoidGoopBlock)
        }
    }

    override fun getColor(state: BlockState?, world: BlockView?, pos: BlockPos?): Int {
        return Color(0f, 0f, 0f, 1f).rgb
    }
}