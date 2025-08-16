package grauly.dustydecor.component

import com.mojang.serialization.Codec
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import io.netty.buffer.ByteBuf
import net.minecraft.component.ComponentsAccess
import net.minecraft.item.Item
import net.minecraft.item.tooltip.TooltipAppender
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.network.codec.PacketCodec
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Colors
import java.util.function.Consumer

object WrenchComponent : TooltipAppender {
    override fun appendTooltip(
        context: Item.TooltipContext,
        textConsumer: Consumer<Text>,
        type: TooltipType,
        components: ComponentsAccess
    ) {
        textConsumer.accept(Text.empty())
        textConsumer.accept(
            Text.translatable(TRANSLATION_KEY_0, ModBlocks.VAC_PIPE.name)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Text.translatable(TRANSLATION_KEY_1)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.LIGHT_GRAY)))
        )
    }

    const val TRANSLATION_KEY_0 = "component.wrench.${DustyDecorMod.MODID}.tooltip0"
    const val TRANSLATION_KEY_1 = "component.wrench.${DustyDecorMod.MODID}.tooltip1"

    val CODEC: Codec<WrenchComponent> = Codec.unit(this)
    val PACKET_CODEC: PacketCodec<ByteBuf, WrenchComponent> = PacketCodec.unit(this)
}