package grauly.dustydecor

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.item.Item
import net.minecraft.registry.tag.TagKey

object ItemDatagenWrapper {
    val entries: MutableList<DatagenSpec> = mutableListOf()
    fun init() {
        entries.add(
            DatagenSpec(
                ModItems.SCREWDRIVER,
                "Screwdriver",
                listOf(ConventionalItemTags.WRENCH_TOOLS),
                generateBaseModel = true
            )
        )
    }

    class DatagenSpec(
        val item: Item,
        val lang: String,
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