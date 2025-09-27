package grauly.dustydecor.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModComponentTypes
import net.minecraft.component.ComponentsAccess
import net.minecraft.item.Item
import net.minecraft.item.tooltip.TooltipAppender
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Colors
import java.util.function.Consumer

data class BulkGoopSizeComponent(var size: Int) : TooltipAppender {
    override fun appendTooltip(
        context: Item.TooltipContext,
        textConsumer: Consumer<Text?>,
        type: TooltipType,
        components: ComponentsAccess
    ) {
        val comp = components.get(ModComponentTypes.VOID_GOOP_SIZE) ?: return
        textConsumer.accept(
            Text.translatable(SIZE_INDICATOR, comp.size, ModBlocks.VOID_GOOP.name)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Text.translatable(EXPLANATION)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Text.translatable(EXPLANATION_L2)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.LIGHT_GRAY)))
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