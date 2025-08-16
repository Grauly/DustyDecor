package grauly.dustydecor

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.item.ToolMaterial
import net.minecraft.registry.tag.BlockTags

object ModToolMaterials {
    val SCREWDRIVER_TOOL_MATERIAL = ToolMaterial(
        BlockTags.INCORRECT_FOR_WOODEN_TOOL,
        150, //Durability
        0.0f, //Attack Speed Offset
        0.0f, //Attack Damage Offset
        1, //Enchantablility
        ConventionalItemTags.IRON_INGOTS
    )
}