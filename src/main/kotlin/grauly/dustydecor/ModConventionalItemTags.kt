package grauly.dustydecor

import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object ModConventionalItemTags {
    val SCREWDRIVER_TOOLS: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, Identifier.of("c", "tools/screwdriver"))
}