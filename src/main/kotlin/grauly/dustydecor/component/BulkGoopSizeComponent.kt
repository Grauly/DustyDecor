package grauly.dustydecor.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModDataComponentTypes
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.world.item.Item
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.item.TooltipFlag
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.util.CommonColors
import java.util.function.Consumer

data class BulkGoopSizeComponent(var size: Int) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        textConsumer: Consumer<Component>,
        type: TooltipFlag,
        components: DataComponentGetter
    ) {
        val comp = components.get(ModDataComponentTypes.VOID_GOOP_SIZE) ?: return
        textConsumer.accept(
            Component.translatable(SIZE_INDICATOR, comp.size, ModBlocks.VOID_GOOP.name)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Component.translatable(EXPLANATION)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Component.translatable(EXPLANATION_L2)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        )
    }

    companion object {
        const val EXPLANATION = "component.bulk_goop_size.${DustyDecorMod.MODID}.explanation0"
        const val EXPLANATION_L2 = "component.bulk_goop_size.${DustyDecorMod.MODID}.explanation1"
        const val SIZE_INDICATOR = "component.bulk_goop_size.${DustyDecorMod.MODID}.description"
        val CODEC: Codec<BulkGoopSizeComponent> = RecordCodecBuilder.create {
            it.group(
                Codec.INT.fieldOf("size").forGetter { goop -> goop.size },
            ).apply(it, ::BulkGoopSizeComponent)
        }
        val DEFAULT = BulkGoopSizeComponent(1)
        const val MAX_SIZE = 1024
    }
}