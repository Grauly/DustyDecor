package grauly.dustydecor.block.layered

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModItemTags
import net.minecraft.core.BlockPos
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import java.awt.Color

class CrimsonSandBlock(threshold: Int, settings: Properties) : LayerThresholdSpreadingBlock(threshold, settings) {
    override fun canBeReplaced(
        state: BlockState,
        context: BlockPlaceContext
    ): Boolean {
        if (context.itemInHand.`is`(ModItemTags.CRIMSON_SAND)) {
            if (state.getValue(LAYERS) < MAX_LAYERS) {
                return true
            }
        }
        return super.canBeReplaced(state, context)
    }

    override fun codec(): MapCodec<out CrimsonSandBlock> {
        return RecordCodecBuilder.mapCodec {
            it.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("threshold").forGetter { block -> block.threshold },
                propertiesCodec()
            ).apply(it, ::CrimsonSandBlock)
        }
    }

    override fun getDustColor(
        blockState: BlockState,
        level: BlockGetter,
        pos: BlockPos
    ): Int {
        return Color(178, 37, 35).rgb
    }
}