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
                it.addAfter(Items.PINK_BUNDLE, ModItems.VAC_CAPSULE)
            }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
            .register {
                it.addAfter(Items.WAXED_OXIDIZED_COPPER_CHEST, ModItems.VAC_PIPE, ModItems.VAC_PIPE_STATION)
            }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS)
            .register {
                it.addAfter(
                    Items.PINK_CANDLE,
                    *ModItems.TALL_CAGE_LAMPS.toTypedArray(),
                    *ModItems.WIDE_CAGE_LAMPS.toTypedArray(),
                    *ModItems.ALARM_CAGE_LAMPS.toTypedArray(),
                    *ModItems.TUBE_LAMPS.toTypedArray(),
                    *ModItems.STOOLS.toTypedArray()
                )
            }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
            .register {
                it.addAfter(Items.POPPED_CHORUS_FRUIT, ModItems.OUTSIDE_CRYSTAL_SHARD)
            }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL)
            .register {
                it.addAfter(Items.SCULK_SENSOR, ModItems.VOID_GOOP, ModItems.BULK_VOID_GOOP)
            }
    }
}