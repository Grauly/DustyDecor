package grauly.dustydecor

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items

object ModItemGroups {
    fun init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS)
            .register {
                it.addAfter(Items.IRON_CHAIN, ModItems.VENT, ModItems.VENT_COVER)
            }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
            .register {
                it.add(ModItems.SCREWDRIVER)
                it.add(ModItems.WRENCH)
            }
    }
}