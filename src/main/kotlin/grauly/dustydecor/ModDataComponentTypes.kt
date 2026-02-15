package grauly.dustydecor

import com.mojang.serialization.Codec
import grauly.dustydecor.component.BulkGoopSizeComponent
import grauly.dustydecor.component.ToolComponents
import grauly.dustydecor.component.WrenchDataComponent
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.TooltipProvider

object ModDataComponentTypes {

    val WRENCH: DataComponentType<WrenchDataComponent> = registerComponent(WrenchDataComponent.CODEC, "wrench")
    val VOID_GOOP_SIZE: DataComponentType<BulkGoopSizeComponent> =
        registerComponent(BulkGoopSizeComponent.CODEC, "bulk_goop")

    val VENT_COVER_LOCK: DataComponentType<TooltipProvider> = registerComponent(ToolComponents.VENT_LOCK_TOGGLE.codec, "vent_cover_lock")
    val VAC_TUBE_WINDOW_TOGGLE: DataComponentType<TooltipProvider> = registerComponent(ToolComponents.VAC_TUBE_WINDOW_TOGGLE.codec, "vac_tube_window_toggle")
    val LAMP_INVERSION: DataComponentType<TooltipProvider> = registerComponent(ToolComponents.LAMPS_INVERT.codec, "lamps_invert")


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