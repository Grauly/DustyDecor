package grauly.dustydecor

import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry

object ModTooltips {

    fun init() {
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.VENT_COVER_LOCK)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.VAC_TUBE_WINDOW_TOGGLE)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.LAMPS_INVERT)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.LAMPS_REPAIR)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.VAC_TUBE_EDIT)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.VAC_STATION_INVERT)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.VOID_GOOP_SIZE)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.SMALL_GLASS_TABLE_STRIP_PANE)
    }
}