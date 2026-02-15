package grauly.dustydecor.component

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import grauly.dustydecor.DustyDecorMod
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.util.CommonColors
import net.minecraft.world.item.component.TooltipProvider

object ToolComponents {

    val VENT_LOCK_TOGGLE = makeToolComponent("component.screwdriver.${DustyDecorMod.MODID}.tooltip.vent_cover")
    val VAC_TUBE_WINDOW_TOGGLE = makeToolComponent("component.screwdriver.${DustyDecorMod.MODID}.tooltip.vac_tube")
    val LAMPS_INVERT = makeToolComponent("component.screwdriver.${DustyDecorMod.MODID}.tooltip.")

    fun makeToolComponent(translationKey: String): ToolComponent {
        val component = Component.translatable(translationKey).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        val provider = TooltipProvider { context, consumer, flag, components -> consumer.accept(component)}
        val codec = MapCodec.unitCodec(provider)
        return ToolComponent(translationKey, provider, codec)
    }

    data class ToolComponent(val translationKey: String, val component: TooltipProvider, val codec: Codec<TooltipProvider>)
}