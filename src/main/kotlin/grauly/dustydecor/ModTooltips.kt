package grauly.dustydecor

import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry

object ModTooltips {

    fun init() {
        ComponentTooltipAppenderRegistry.addFirst(ModComponentTypes.WRENCH)
        ComponentTooltipAppenderRegistry.addFirst(ModComponentTypes.SCREWDRIVER)
    }
}