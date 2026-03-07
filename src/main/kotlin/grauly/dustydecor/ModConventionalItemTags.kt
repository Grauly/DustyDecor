package grauly.dustydecor

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object ModConventionalItemTags {
    val SCREWDRIVER_TOOLS: TagKey<Item> =
        TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/screwdriver"))
}