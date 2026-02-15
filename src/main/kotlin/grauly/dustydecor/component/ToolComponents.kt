package grauly.dustydecor.component

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.util.CommonColors
import net.minecraft.world.item.component.TooltipProvider

object ToolComponents {

    val VENT_LOCK_TOGGLE = makeToolComponent(
        "component.screwdriver.${DustyDecorMod.MODID}.tooltip.vent_cover",
        ModBlocks.VENT_COVER.name
    )
    val VAC_TUBE_WINDOW_TOGGLE = makeToolComponent(
        "component.screwdriver.${DustyDecorMod.MODID}.tooltip.vac_tube",
        ModBlocks.VAC_PIPE.name
    )
    val VAC_TUBE_EDIT = makeMultilineComponent(
        mapOf(
            "component.wrench.${DustyDecorMod.MODID}.tooltip.vac_pipe.edit" to listOf(ModBlocks.VAC_PIPE.name),
            "component.wrench.${DustyDecorMod.MODID}.vac_pipe.connection" to listOf(),
            "component.wrench.${DustyDecorMod.MODID}.vap_pipe.fix" to listOf(),
        )
    )
    val VAC_STATION_INVERT = makeToolComponent("component.wrench.${DustyDecorMod.MODID}.vac_station.toggle", ModBlocks.VAC_PIPE_STATION.name)
    val LAMPS_INVERT = makeToolComponent("component.screwdriver.${DustyDecorMod.MODID}.tooltip.")
    val LAMPS_REPAIR = makeToolComponent("component.wrench.${DustyDecorMod.MODID}.lamps")

    fun makeToolComponent(translationKey: String, vararg insertions: Component): ToolComponent {
        val component = Component.translatable(translationKey, *insertions)
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        val provider = TooltipProvider { context, consumer, flag, components -> consumer.accept(component) }
        val codec = MapCodec.unitCodec(provider)
        return ToolComponent(translationKey, provider, codec)
    }

    fun makeMultilineComponent(lines: Map<String, List<Component>>): MultiLineToolComponent {
        val components = lines.map { (translationKey, insertions) ->
            Component.translatable(translationKey, *insertions.toTypedArray())
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(CommonColors.LIGHT_GRAY)))
        }
        val provider = TooltipProvider { context, consumer, flag, getter -> components.forEach { consumer.accept(it) } }
        val codec = MapCodec.unitCodec(provider)
        return MultiLineToolComponent(lines.keys.toList(), provider, codec)
    }

    data class ToolComponent(
        val translationKey: String,
        val component: TooltipProvider,
        val codec: Codec<TooltipProvider>
    )

    data class MultiLineToolComponent(
        val lines: List<String>,
        val component: TooltipProvider,
        val codec: Codec<TooltipProvider>
    )
}