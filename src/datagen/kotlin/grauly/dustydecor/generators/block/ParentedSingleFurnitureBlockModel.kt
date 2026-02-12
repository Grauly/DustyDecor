package grauly.dustydecor.generators.block

import com.mojang.math.Quadrant
import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.renderer.block.model.Material
import net.minecraft.client.renderer.block.model.VariantMutator
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.*
import kotlin.math.floor

class ParentedSingleFurnitureBlockModel(
    private val blocks: List<SingleFurnitureBlock>,
    private val replacements: List<Identifier>,
    private val baseModel: Identifier
) {
    fun get(blockStateModelGenerator: BlockModelGenerators) {
        blocks.forEach {
            createFurniture(it, replacements[blocks.indexOf(it)], blockStateModelGenerator)
        }
    }

    fun createFurniture(block: SingleFurnitureBlock, replaceTexture: Identifier, generator: BlockModelGenerators) {
        createBlock(block, replaceTexture, generator)
        createItem(block, replaceTexture, generator)
    }

    fun createBlock(block: SingleFurnitureBlock, replaceTexture: Identifier, generator: BlockModelGenerators) {
        val variantList = listOf(0,1,2,3).map { variant ->
            val variantBaseModel = Identifier.fromNamespaceAndPath(baseModel.namespace, "${baseModel.path}_$variant");
            val mapping = TextureMapping.singleSlot(
                GLASS_TABLE_TEXTURE_SLOT,
                Material(replaceTexture),
            )
            val template = ModelTemplate(
                Optional.of(variantBaseModel),
                Optional.empty(),
                GLASS_TABLE_TEXTURE_SLOT
            )
            template.createWithSuffix(block,"_$variant", mapping, generator.modelOutput)
        }

        generator.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.ROTATION_16).generate {
                    val variant = it % 4
                    val baseRotation = floor(it / 4.0).toInt()
                    val rotationQuadrant = Quadrant.entries[baseRotation]
                    val model = variantList[variant]
                    BlockModelDatagen.singleVariant(model)
                        .with(VariantMutator.Y_ROT.withValue(rotationQuadrant))
                })
        )
    }

    fun createItem(block: SingleFurnitureBlock, replaceTexture: Identifier, generator: BlockModelGenerators) {
        //[Space intentionally left blank]
    }

    val GLASS_TABLE_TEXTURE_SLOT = TextureSlot.create("1")
}