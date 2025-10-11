package grauly.dustydecor

import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object ModDamageTypes {
    val VOID_CONSUMPTION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(DustyDecorMod.MODID, "void_consumption"))
}