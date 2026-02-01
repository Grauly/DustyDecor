package grauly.dustydecor

import com.mojang.serialization.Codec
import grauly.dustydecor.component.BulkGoopSizeComponent
import grauly.dustydecor.component.ScrewdriverDataComponent
import grauly.dustydecor.component.WrenchDataComponent
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry
import net.minecraft.resources.Identifier

object ModDataComponentTypes {

    val SCREWDRIVER: DataComponentType<ScrewdriverDataComponent> = registerComponent(ScrewdriverDataComponent.CODEC, "screwdriver")
    val WRENCH: DataComponentType<WrenchDataComponent> = registerComponent(WrenchDataComponent.CODEC, "wrench")
    val VOID_GOOP_SIZE: DataComponentType<BulkGoopSizeComponent> = registerComponent(BulkGoopSizeComponent.CODEC, "bulk_goop")

    private fun <T: Any> registerComponent(
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