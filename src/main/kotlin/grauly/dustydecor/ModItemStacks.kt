package grauly.dustydecor

import grauly.dustydecor.block.LayerThresholdSpreadingBlock
import net.minecraft.component.ComponentChanges
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.BlockStateComponent
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier

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
            ).add(
                DataComponentTypes.ITEM_MODEL,
                Identifier.of(DustyDecorMod.MODID, "block/void_goop/void_goop_8")
            )
            .build()
    )
    const val VOID_GOOP_8_LAYER_DESCRIPTION: String = "item.void_goop.full.description"
}