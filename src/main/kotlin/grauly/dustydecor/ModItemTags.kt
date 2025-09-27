package grauly.dustydecor

import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object ModItemTags {
    val VOID_GOOP: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, Identifier.of(DustyDecorMod.MODID, "void_goop"))
}