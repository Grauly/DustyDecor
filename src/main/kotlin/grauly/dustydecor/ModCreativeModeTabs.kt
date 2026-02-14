package grauly.dustydecor

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Items

object ModCreativeModeTabs {
    fun init() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
            .register {
                it.insertAfter(Items.IRON_CHAIN, ModItems.VENT, ModItems.VENT_COVER)
            }
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register {
                it.insertAfter(Items.BRUSH, ModItems.SCREWDRIVER, ModItems.WRENCH)
                it.insertAfter(Items.PINK_BUNDLE, ModItems.VAC_CAPSULE)
            }
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
            .register {
                it.insertAfter(Items.WAXED_OXIDIZED_COPPER_CHEST, ModItems.VAC_PIPE, ModItems.VAC_PIPE_STATION)
            }
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COLORED_BLOCKS)
            .register {
                it.insertAfter(
                    Items.PINK_CANDLE,
                    *ModItems.TALL_CAGE_LAMPS.toTypedArray(),
                    *ModItems.WIDE_CAGE_LAMPS.toTypedArray(),
                    *ModItems.ALARM_CAGE_LAMPS.toTypedArray(),
                    *ModItems.TUBE_LAMPS.toTypedArray(),
                    *ModItems.STOOLS.toTypedArray(),
                    *ModItems.CHAIRS.toTypedArray(),
                    ModItems.GLASS_TABLE,
                    *ModItems.GLASS_TABLES.toTypedArray(),
                    ModItems.GLASS_TABLE_FRAME,
                )
            }
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register {
                it.insertAfter(Items.POPPED_CHORUS_FRUIT, ModItems.OUTSIDE_CRYSTAL_SHARD)
            }
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
            .register {
                it.insertAfter(Items.SCULK_SENSOR, ModItems.VOID_GOOP, ModItems.BULK_VOID_GOOP)
            }
    }
}