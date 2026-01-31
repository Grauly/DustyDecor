package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import grauly.dustydecor.generators.BlockModelDatagen
import grauly.dustydecor.util.DyeUtils
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.resources.ResourceLocation

open class SingleFurnitureBlockModel(private val blocks: List<SingleFurnitureBlock>, private val modelPrefix: String) {
    fun get(blockStateModelGenerator: BlockModelGenerators) {
        blocks.forEach{
            createFurniture(it, blockStateModelGenerator)
        }
    }

    protected fun createFurniture(block: SingleFurnitureBlock, blockStateModelGenerator: BlockModelGenerators) {
        createBlock(block, blockStateModelGenerator)
        createItem(block, blockStateModelGenerator)
    }

    protected fun createBlock(block: SingleFurnitureBlock, blockStateModelGenerator: BlockModelGenerators) {
        blockStateModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(block, MODEL)
            //TODO: actual rotations
        )
    }

    protected fun createItem(block: SingleFurnitureBlock, blockStateModelGenerator: BlockModelGenerators) {
        val color = DyeUtils.COLOR_ORDER[blocks.indexOf(block)].textColor
        val tint = ItemModelUtils.constantTint(color)
        val model = ItemModelUtils.tintedModel(ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, MODEL_PATH), tint)
        blockStateModelGenerator.itemModelOutput.accept(block.asItem(), model)
    }

    protected val MODEL_PATH = "block/$modelPrefix"
    protected val MODEL = BlockModelDatagen.singleVariant(MODEL_PATH)
}