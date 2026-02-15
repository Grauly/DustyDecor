package grauly.dustydecor

import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry

object ModTooltips {

    fun init() {
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.WRENCH)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.VENT_COVER_LOCK)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.VAC_TUBE_WINDOW_TOGGLE)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.LAMP_INVERSION)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.VOID_GOOP_SIZE)
    }
}