package grauly.dustydecor

import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry


object ModTooltips {

    fun init() {
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.WRENCH)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.SCREWDRIVER)
        ItemComponentTooltipProviderRegistry.addFirst(ModDataComponentTypes.VOID_GOOP_SIZE)
    }
}