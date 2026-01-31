package grauly.dustydecor

import net.minecraft.world.item.Item
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.resources.Identifier

object ModConventionalItemTags {
    val SCREWDRIVER_TOOLS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/screwdriver"))
}