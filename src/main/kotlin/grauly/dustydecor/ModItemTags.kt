package grauly.dustydecor

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object ModItemTags {
    val VOID_GOOP: TagKey<Item> = register("void_goop")
    val CRIMSON_SAND: TagKey<Item> = register("crimson_sand")

    private fun register(identifier: Identifier): TagKey<Item> =
        TagKey.create(Registries.ITEM, identifier)

    private fun register(path: String): TagKey<Item> =
        register(Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, path))
}