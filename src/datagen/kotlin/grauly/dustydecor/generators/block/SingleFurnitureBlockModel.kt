package grauly.dustydecor.generators.block

import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator

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
        //TODO: figure out if I need this
    }

    protected val MODEL = BlockModelDatagen.singleVariant("block/$modelPrefix")
}