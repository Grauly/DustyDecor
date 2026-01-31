package grauly.dustydecor

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.world.item.Item
import net.minecraft.tags.TagKey

object ItemDatagenWrapper {
    val entries: MutableList<DatagenSpec> = mutableListOf()
    fun init() {
        item(
            ModItems.SCREWDRIVER,
            "Screwdriver",
            listOf(ModConventionalItemTags.SCREWDRIVER_TOOLS),
            generateBaseModel = true
        )
        item(
            ModItems.WRENCH,
            "Wrench",
            listOf(ConventionalItemTags.WRENCH_TOOLS),
            generateBaseModel = true
        )
        item (
            ModItems.VAC_CAPSULE,
            "Vacuum Tube Capsule"
        )
        item(
            ModItems.OUTSIDE_CRYSTAL_SHARD,
            "Outside Crystal Shard",
            generateBaseModel = true
        )
        item(
            ModItems.BULK_VOID_GOOP,
            "Bulk Void Goop",
            listOf(ModItemTags.VOID_GOOP),
            generateBaseModel = true
        )
        item(
            ModItems.VOID_GOOP,
            null,
            listOf(ModItemTags.VOID_GOOP)
        )
    }

    private fun item(
        item: Item,
        lang: String?,
        tags: List<TagKey<Item>> = listOf(),
        toolSpec: ToolSpec = ToolSpec(),
        generateBaseModel: Boolean = false
    ) {
        entries.add(
            DatagenSpec(item, lang, tags, toolSpec, generateBaseModel)
        )
    }

    class DatagenSpec(
        val item: Item,
        val lang: String?,
        val tags: List<TagKey<Item>> = listOf(),
        val toolSpec: ToolSpec = ToolSpec(),
        val generateBaseModel: Boolean = false
    )

    class ToolSpec(
        val sword: Boolean = false,
        val pickaxe: Boolean = false,
        val axe: Boolean = false,
        val shovel: Boolean = false,
        val hoe: Boolean = false
    ) {
        fun needsProcessing() = sword || pickaxe || axe || shovel || hoe
    }
}