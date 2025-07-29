package grauly.dustydecor

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items

object ModItemGroups {
    fun init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS)
            .register {
                it.addAfter(Items.CHAIN, ModItems.VENT, ModItems.VENT_COVER)
            }
    }
}