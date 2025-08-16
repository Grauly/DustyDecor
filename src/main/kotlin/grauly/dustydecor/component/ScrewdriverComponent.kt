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
import net.minecraft.text.Text
import java.util.function.Consumer

object ScrewdriverComponent: TooltipAppender {
    override fun appendTooltip(
        context: Item.TooltipContext,
        textConsumer: Consumer<Text>,
        type: TooltipType,
        components: ComponentsAccess
    ) {
        textConsumer.accept(Text.translatable(VENT_COVER_TRANSLATION_KEY, ModBlocks.VENT_COVER.name))
        textConsumer.accept(Text.translatable(VAC_TUBE_TRANSLATION_KEY, ModBlocks.VAC_PIPE.name))
    }

    const val VENT_COVER_TRANSLATION_KEY = "component.screwdriver.${DustyDecorMod.MODID}.tooltip.vent_cover"
    const val VAC_TUBE_TRANSLATION_KEY = "component.screwdriver.${DustyDecorMod.MODID}.tooltip.vac_tube"

    val CODEC: Codec<ScrewdriverComponent> = Codec.unit(this)
    val PACKET_CODEC: PacketCodec<ByteBuf, ScrewdriverComponent> = PacketCodec.unit(this)
}