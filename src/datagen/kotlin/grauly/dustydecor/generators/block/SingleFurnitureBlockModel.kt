package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import grauly.dustydecor.generators.BlockModelDatagen
import grauly.dustydecor.util.DyeUtils
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModels
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator
import net.minecraft.util.Identifier

open class SingleFurnitureBlockModel(private val blocks: List<SingleFurnitureBlock>, private val modelPrefix: String) {
    fun get(blockStateModelGenerator: BlockStateModelGenerator) {
        blocks.forEach{
            createFurniture(it, blockStateModelGenerator)
        }
    }

    protected fun createFurniture(block: SingleFurnitureBlock, blockStateModelGenerator: BlockStateModelGenerator) {
        createBlock(block, blockStateModelGenerator)
        createItem(block, blockStateModelGenerator)
    }

    protected fun createBlock(block: SingleFurnitureBlock, blockStateModelGenerator: BlockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(
            VariantsBlockModelDefinitionCreator.of(block, MODEL)
            //TODO: actual rotations
        )
    }

    protected fun createItem(block: SingleFurnitureBlock, blockStateModelGenerator: BlockStateModelGenerator) {
        val color = DyeUtils.COLOR_ORDER[blocks.indexOf(block)].signColor
        val tint = ItemModels.constantTintSource(color)
        val model = ItemModels.tinted(Identifier.of(DustyDecorMod.MODID, MODEL_PATH), tint)
        blockStateModelGenerator.itemModelOutput.accept(block.asItem(), model)
    }

    protected val MODEL_PATH = "block/$modelPrefix"
    protected val MODEL = BlockModelDatagen.singleVariant(MODEL_PATH)
}