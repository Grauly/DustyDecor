package grauly.dustydecor

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator

class ItemModelDatagen(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
    }

    override fun generateBlockStateModels(p0: BlockStateModelGenerator?) {
        //[Intentionally Left Blank
    }

    override fun getName(): String = "Item Model Definitions"
}