package grauly.dustydecor.component

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import io.netty.buffer.ByteBuf
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.world.item.Item
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.item.TooltipFlag
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.util.CommonColors
import java.util.function.Consumer

object WrenchDataComponent : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        textConsumer: Consumer<Component>,
        type: TooltipFlag,
        components: DataComponentGetter
    ) {
        textConsumer.accept(Component.empty())
        textConsumer.accept(
            Component.translatable(VAC_PIPE_TRANSLATION_KEY, ModBlocks.VAC_PIPE.name)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Component.translatable(VAC_PIPE_CONNECTION_TRANSLATION_KEY)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Component.translatable(VAP_PIPE_CONNECTION_FIX_TRANSLATION_KEY)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Component.translatable(VAC_PIPE_STATION_TRANSLATION_KEY, ModBlocks.VAC_PIPE_STATION.name)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Component.translatable(LAMPS_TRANSLATION_KEY)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        )
    }

    const val VAC_PIPE_TRANSLATION_KEY = "component.wrench.${DustyDecorMod.MODID}.tooltip.vac_pipe.edit"
    const val VAC_PIPE_CONNECTION_TRANSLATION_KEY = "component.wrench.${DustyDecorMod.MODID}.vac_pipe.connection"
    const val VAP_PIPE_CONNECTION_FIX_TRANSLATION_KEY = "component.wrench.${DustyDecorMod.MODID}.vap_pipe.fix"
    const val VAC_PIPE_STATION_TRANSLATION_KEY = "component.wrench.${DustyDecorMod.MODID}.vac_station.toggle"
    const val LAMPS_TRANSLATION_KEY = "component.wrench.${DustyDecorMod.MODID}.lamps"

    val CODEC: Codec<WrenchDataComponent> = MapCodec.unitCodec(this)
    val PACKET_CODEC: StreamCodec<ByteBuf, WrenchDataComponent> = StreamCodec.unit(this)
}