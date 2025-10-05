package grauly.dustydecor

import grauly.dustydecor.BlockDatagenWrapper.MinedBy.*
import grauly.dustydecor.BlockDatagenWrapper.ToolNeed.*
import grauly.dustydecor.util.DyeUtils
import net.minecraft.block.Block
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey

object BlockDatagenWrapper {
    val entries: MutableList<DatagenSpec> = mutableListOf()

    fun init() {
        entries.addAll(
            listOf(
                DatagenSpec(ModBlocks.VENT, "Vent", STONE, PICKAXE),
                DatagenSpec(ModBlocks.VENT_COVER, "Vent Grate", STONE, PICKAXE),
                DatagenSpec(ModBlocks.VAC_PIPE, "Vacuum Tube", STONE, PICKAXE),
                DatagenSpec(ModBlocks.VAC_PIPE_STATION, "Vacuum Tube Station", STONE, PICKAXE),
                DatagenSpec(ModBlocks.VOID_GOOP, "Void Goop")
            )
        )
        entries.addAll(DyeUtils.COLOR_ORDER.map {
            val lookupValue = DyeUtils.COLOR_ORDER.indexOf(it)
            val colorString =
                it.id.split("_").map { s -> s.replaceFirstChar { c -> c.titlecase() } }.reduce { acc, s -> "$acc $s" }
            return@map mutableListOf(
                DatagenSpec(ModBlocks.TALL_CAGE_LAMPS[lookupValue], "Tall $colorString Cage Lamp", STONE, PICKAXE),
                DatagenSpec(ModBlocks.WIDE_CAGE_LAMPS[lookupValue], "Wide $colorString Cage Lamp", STONE, PICKAXE),
                DatagenSpec(ModBlocks.ALARM_CAGE_LAMPS[lookupValue], "$colorString Alarm Cage Lamp", STONE, PICKAXE),
                DatagenSpec(ModBlocks.TUBE_LAMPS[lookupValue], "$colorString Tube Lamp", STONE, PICKAXE),
            )
        }.reduce { acc: MutableList<DatagenSpec>, elem: MutableList<DatagenSpec> -> acc.addAll(elem); acc })
    }

    class DatagenSpec(
        val block: Block,
        val name: String,
        val toolNeed: ToolNeedSpec = ToolNeedSpec(NONE),
        val mineable: MiningNeedSpec = MiningNeedSpec(listOf(ANY)),
        val generateLootTable: Boolean = true,
        val generateCubeAllModel: Boolean = false
    ) {
        constructor(
            block: Block,
            name: String,
            toolNeed: ToolNeed,
            mineable: MinedBy,
            generateLootTable: Boolean = true,
            generateBlockBlockModel: Boolean = false
        ) : this(
            block, name, ToolNeedSpec(toolNeed), MiningNeedSpec(mineable), generateLootTable, generateBlockBlockModel
        )
    }

    class ToolNeedSpec(val toolNeed: ToolNeed, val override: TagKey<Block>? = null) {
        fun needsProcessing(): Boolean {
            return getToolNeed(toolNeed) != null || override != null
        }
    }

    class MiningNeedSpec(val tools: List<MinedBy>, val override: List<TagKey<Block>> = listOf()) {
        constructor(toolNeed: MinedBy) : this(listOf(toolNeed))
        constructor(override: TagKey<Block>) : this(listOf(), listOf(override))
        constructor(override: List<TagKey<Block>>) : this(listOf(), override)

        fun needsProcessing(): Boolean {
            return tools.any { getMineable(it) != null } || override.isNotEmpty()
        }
    }

    enum class ToolNeed {
        NONE, WOOD, STONE, COPPER, GOLD, IRON, DIAMOND, NETHERITE;
    }

    fun getToolNeed(need: ToolNeed): TagKey<Block>? = when (need) {
        STONE, GOLD, COPPER -> BlockTags.NEEDS_STONE_TOOL
        IRON -> BlockTags.NEEDS_IRON_TOOL
        DIAMOND, NETHERITE -> BlockTags.NEEDS_DIAMOND_TOOL
        NONE, WOOD -> null
    }

    enum class MinedBy {
        PICKAXE, AXE, SHOVEL, HOE, ANY;
    }

    fun getMineable(mineing: MinedBy): TagKey<Block>? = when (mineing) {
        PICKAXE -> BlockTags.PICKAXE_MINEABLE
        AXE -> BlockTags.AXE_MINEABLE
        SHOVEL -> BlockTags.SHOVEL_MINEABLE
        HOE -> BlockTags.HOE_MINEABLE
        ANY -> null
    }

}