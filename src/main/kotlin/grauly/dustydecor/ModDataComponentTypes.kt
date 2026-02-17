package grauly.dustydecor

import com.mojang.serialization.Codec
import grauly.dustydecor.component.BulkGoopSizeComponent
import grauly.dustydecor.component.ToolComponents
import grauly.dustydecor.component.ToolUseSoundComponent
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.TooltipProvider

object ModDataComponentTypes {

    val VOID_GOOP_SIZE: DataComponentType<BulkGoopSizeComponent> = registerComponent(BulkGoopSizeComponent.CODEC, "bulk_goop")
    val TOOL_USE_SOUND: DataComponentType<ToolUseSoundComponent> = registerComponent(ToolUseSoundComponent.CODEC, "tool_use_sound")

    val VENT_COVER_LOCK: DataComponentType<TooltipProvider> = registerComponent(ToolComponents.VENT_LOCK_TOGGLE.codec, "vent_cover_lock")
    val VAC_TUBE_WINDOW_TOGGLE: DataComponentType<TooltipProvider> = registerComponent(ToolComponents.VAC_TUBE_WINDOW_TOGGLE.codec, "vac_tube_window_toggle")
    val VAC_TUBE_EDIT: DataComponentType<TooltipProvider> = registerComponent(ToolComponents.VAC_TUBE_EDIT.codec, "vac_tube_edit")
    val LAMPS_INVERT: DataComponentType<TooltipProvider> = registerComponent(ToolComponents.LAMPS_INVERT.codec, "lamps_invert")
    val LAMPS_REPAIR: DataComponentType<TooltipProvider> = registerComponent(ToolComponents.LAMPS_REPAIR.codec, "lamps_repair")
    val VAC_STATION_INVERT: DataComponentType<TooltipProvider> = registerComponent(ToolComponents.VAC_STATION_INVERT.codec, "vac_station.invert")
    val SMALL_GLASS_TABLE_STRIP_PANE: DataComponentType<TooltipProvider> = registerComponent(ToolComponents.SMALL_GLASS_TABLE_STRIP_PANE.codec, "small_glass_table_strip_pane")


    private fun <T : Any> registerComponent(
        codec: Codec<T>,
        id: String,
        namespace: String = DustyDecorMod.MODID
    ): DataComponentType<T> {
        return Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(namespace, id),
            DataComponentType.builder<T>().persistent(codec).build()
        )
    }

    fun init() {
        //[Space intentionally left blank]
    }
}