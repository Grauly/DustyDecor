package grauly.dustydecor

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object ModItemTags {
    val VOID_GOOP: TagKey<Item> =
        TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "void_goop"))
}