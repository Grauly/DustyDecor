package grauly.dustydecor

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey

object ModDamageTypes {
    val VOID_CONSUMPTION = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "void_consumption")
    )
}