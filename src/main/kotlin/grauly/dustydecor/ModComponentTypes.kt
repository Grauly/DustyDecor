package grauly.dustydecor

import com.mojang.serialization.Codec
import grauly.dustydecor.component.ScrewdriverComponent
import grauly.dustydecor.component.WrenchComponent
import net.minecraft.component.ComponentType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModComponentTypes {

    val SCREWDRIVER: ComponentType<ScrewdriverComponent> = registerComponent(ScrewdriverComponent.CODEC, "screwdriver")
    val WRENCH: ComponentType<WrenchComponent> = registerComponent(WrenchComponent.CODEC, "wrench")

    private fun <T> registerComponent(
        codec: Codec<T>,
        id: String,
        namespace: String = DustyDecorMod.MODID
    ): ComponentType<T> {
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(namespace, id),
            ComponentType.builder<T>().codec(codec).build()
        )
    }

    fun init() {
        //[Space intentionally left blank]
    }
}