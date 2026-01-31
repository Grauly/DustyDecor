package grauly.dustydecor.generators

import grauly.dustydecor.ItemDatagenWrapper
import grauly.dustydecor.ModItems
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ModelTemplates

class ItemModelDatagen(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateItemModels(itemModelGenerator: ItemModelGenerators) {
        ItemDatagenWrapper.entries.filter { it.generateBaseModel }.forEach {
            itemModelGenerator.generateFlatItem(it.item, ModelTemplates.FLAT_HANDHELD_ITEM)
        }
        itemModelGenerator.generateSpyglass(ModItems.VAC_CAPSULE)
    }

    override fun generateBlockStateModels(p0: BlockModelGenerators?) {
        //[Intentionally Left Blank]
    }

    override fun getName(): String = "Item Model Definitions"
}