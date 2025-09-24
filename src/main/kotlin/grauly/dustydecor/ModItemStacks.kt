package grauly.dustydecor

import grauly.dustydecor.block.LayerThresholdSpreadingBlock
import net.minecraft.component.ComponentChanges
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.BlockStateComponent
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.Text

object ModItemStacks {
    val VOID_GOOP_8_LAYER = ItemStack(
        Registries.ITEM.getEntry(ModItems.VOID_GOOP),
        1,
        ComponentChanges.builder()
            .add(
                DataComponentTypes.BLOCK_STATE,
                BlockStateComponent(mapOf(LayerThresholdSpreadingBlock.LAYERS.name to "${LayerThresholdSpreadingBlock.MAX_LAYERS}"))
            ).add(
                DataComponentTypes.LORE,
                LoreComponent(listOf(Text.translatable(VOID_GOOP_8_LAYER_DESCRIPTION, ModItems.VOID_GOOP.name)))
            )
            .build()
    )
    const val VOID_GOOP_8_LAYER_DESCRIPTION: String = "item.void_goop.full.description"
}