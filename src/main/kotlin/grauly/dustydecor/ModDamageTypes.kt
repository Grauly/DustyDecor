package grauly.dustydecor

import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation

object ModDamageTypes {
    val VOID_CONSUMPTION = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, "void_consumption"))
}