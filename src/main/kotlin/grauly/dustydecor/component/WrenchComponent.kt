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
            Text.translatable(VAC_PIPE_TRANSLATION_KEY, ModBlocks.VAC_PIPE.name)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Text.translatable(VAC_PIPE_CONNECTION_TRANSLATION_KEY)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Text.translatable(VAC_PIPE_STATION_TRANSLATION_KEY, ModBlocks.VAC_PIPE_STATION.name)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Text.translatable(LAMPS_TRANSLATION_KEY)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.LIGHT_GRAY)))
        )
    }

    const val VAC_PIPE_TRANSLATION_KEY = "component.wrench.${DustyDecorMod.MODID}.tooltip.vac_pipe.edit"
    const val VAC_PIPE_CONNECTION_TRANSLATION_KEY = "component.wrench.${DustyDecorMod.MODID}.vac_pipe.connection"
    const val VAC_PIPE_STATION_TRANSLATION_KEY = "component.wrench.${DustyDecorMod.MODID}.vac_station.toggle"
    const val LAMPS_TRANSLATION_KEY = "component.wrench.${DustyDecorMod.MODID}.lamps"

    val CODEC: Codec<WrenchComponent> = Codec.unit(this)
    val PACKET_CODEC: PacketCodec<ByteBuf, WrenchComponent> = PacketCodec.unit(this)
}