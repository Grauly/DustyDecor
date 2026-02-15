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
        tooltip("vent_cover_toggle"),
        ModBlocks.VENT_COVER.name
    )
    val VAC_TUBE_WINDOW_TOGGLE = makeToolComponent(
        tooltip("vac_tube_window_toggle"),
        ModBlocks.VAC_PIPE.name
    )
    val VAC_TUBE_EDIT = makeMultilineComponent(
        mapOf(
            tooltip("vac_tube_edit.can_edit") to listOf(ModBlocks.VAC_PIPE.name),
            tooltip("vac_tube_edit.can_redirect_connections") to listOf(),
            tooltip("vac_tube_edit.can_fix_connections") to listOf(),
        )
    )
    val VAC_STATION_INVERT = makeToolComponent(
        tooltip("vac_station_invert"),
        ModBlocks.VAC_PIPE_STATION.name
    )
    val LAMPS_INVERT = makeToolComponent(tooltip("lamps_invert"))
    val LAMPS_REPAIR = makeToolComponent(tooltip("lamps_repair"))

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

    fun tooltip(snippet: String): String =
        "component.${DustyDecorMod.MODID}.$snippet.tooltip"

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