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
                it.addAfter(Items.BRUSH, ModItems.SCREWDRIVER, ModItems.WRENCH)
            }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
            .register {
                it.addAfter(Items.WAXED_OXIDIZED_COPPER_CHEST, ModItems.VAC_PIPE)
            }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS)
            .register {
                it.addAfter(Items.PINK_CANDLE, *ModItems.TALL_CAGE_LAMPS.toTypedArray(), *ModItems.WIDE_CAGE_LAMPS.toTypedArray())
            }
    }
}