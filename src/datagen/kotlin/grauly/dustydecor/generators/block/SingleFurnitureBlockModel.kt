package grauly.dustydecor.generators.block

import com.mojang.math.Quadrant
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import grauly.dustydecor.generators.BlockModelDatagen
import grauly.dustydecor.util.DyeUtils
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.renderer.block.model.VariantMutator
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import kotlin.math.floor

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
            MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(BlockStateProperties.ROTATION_16).generate {
                val rotationIndex = it % 4
                val baseRotation = floor(it / 4.0).toInt()
                val rotationQuadrant = Quadrant.entries[baseRotation]
                getModel(rotationIndex).with(VariantMutator.Y_ROT.withValue(rotationQuadrant))
            })
        )
    }

    protected fun createItem(block: SingleFurnitureBlock, blockStateModelGenerator: BlockModelGenerators) {
        val color = DyeUtils.COLOR_ORDER[blocks.indexOf(block)].textColor
        val tint = ItemModelUtils.constantTint(color)
        val model = ItemModelUtils.tintedModel(Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "${MODEL_PATH}_0"), tint)
        blockStateModelGenerator.itemModelOutput.accept(block.asItem(), model)
    }

    protected val MODEL_PATH = "block/$modelPrefix"
    private fun getModel(rotationOffset: Int) = BlockModelDatagen.singleVariant("${MODEL_PATH}_$rotationOffset")
}