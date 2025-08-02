package grauly.dustydecor.generators

import grauly.dustydecor.ItemDatagenWrapper
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.Models

class ItemModelDatagen(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        ItemDatagenWrapper.entries.filter { it.generateBaseModel }.forEach {
            itemModelGenerator.register(it.item, Models.HANDHELD)
        }
    }

    override fun generateBlockStateModels(p0: BlockStateModelGenerator?) {
        //[Intentionally Left Blank]
    }

    override fun getName(): String = "Item Model Definitions"
}