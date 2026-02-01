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
import net.minecraft.util.Unit
import java.util.function.Consumer

object ScrewdriverDataComponent : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        textConsumer: Consumer<Component>,
        type: TooltipFlag,
        components: DataComponentGetter
    ) {
        textConsumer.accept(Component.empty())
        textConsumer.accept(
            Component.translatable(VENT_COVER_TRANSLATION_KEY, ModBlocks.VENT_COVER.name)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        )
        textConsumer.accept(
            Component.translatable(VAC_TUBE_TRANSLATION_KEY, ModBlocks.VAC_PIPE.name)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        )
    }

    const val VENT_COVER_TRANSLATION_KEY = "component.screwdriver.${DustyDecorMod.MODID}.tooltip.vent_cover"
    const val VAC_TUBE_TRANSLATION_KEY = "component.screwdriver.${DustyDecorMod.MODID}.tooltip.vac_tube"
    const val LAMPS_TRANSLATION_KEY = "component.screwdriver.${DustyDecorMod.MODID}.tooltip."

    val CODEC: Codec<ScrewdriverDataComponent> = MapCodec.unitCodec(this)
    val PACKET_CODEC: StreamCodec<ByteBuf, ScrewdriverDataComponent> = StreamCodec.unit(this)
}