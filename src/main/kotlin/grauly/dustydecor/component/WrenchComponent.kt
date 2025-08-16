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

object WrenchComponent: TooltipAppender{
    override fun appendTooltip(
        context: Item.TooltipContext,
        textConsumer: Consumer<Text>,
        type: TooltipType,
        components: ComponentsAccess
    ) {
        textConsumer.accept(Text.translatable(TRANSLATION_KEY, ModBlocks.VAC_PIPE.name))
    }
    const val TRANSLATION_KEY = "component.wrench.${DustyDecorMod.MODID}.tooltip"

    val CODEC: Codec<WrenchComponent> = Codec.unit(this)
    val PACKET_CODEC: PacketCodec<ByteBuf, WrenchComponent> = PacketCodec.unit(this)
}