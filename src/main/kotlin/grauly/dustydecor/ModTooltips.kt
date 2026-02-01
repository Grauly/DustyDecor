package grauly.dustydecor

import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry

object ModTooltips {

    fun init() {
        ComponentTooltipAppenderRegistry.addFirst(ModDataComponentTypes.WRENCH)
        ComponentTooltipAppenderRegistry.addFirst(ModDataComponentTypes.SCREWDRIVER)
        ComponentTooltipAppenderRegistry.addFirst(ModDataComponentTypes.VOID_GOOP_SIZE)
    }
}