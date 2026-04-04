package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import java.util.Optional

object CrimsonSandBlockModel {
    fun get(blockStateModelGenerator: BlockModelGenerators) {
        val creator = MultiVariantGenerator.dispatch(ModBlocks.CRIMSON_SAND)
        val layersMap = PropertyDispatch.initial(BlockStateProperties.LAYERS).generate { layers ->
            val variantBaseModel = Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "${BASE_MODEL_PATH}_$layers")
            val mapping = TextureMapping.singleSlot(
                BASE_SLOT,
                Material(REPLACE_TEXTURE),
            ).put(
                PARTICLE_SLOT,
                Material(REPLACE_TEXTURE),
            )
            val template = ModelTemplate(
                Optional.of(variantBaseModel),
                Optional.empty(),
                BASE_SLOT, PARTICLE_SLOT
            )
            val model = template.createWithSuffix(ModBlocks.CRIMSON_SAND, "_$layers", mapping, blockStateModelGenerator.modelOutput)
            BlockModelDatagen.singleVariant(model)
        }
        blockStateModelGenerator.blockStateOutput.accept(creator.with(layersMap))
        blockStateModelGenerator.registerSimpleFlatItemModel(ModItems.CRIMSON_SAND)
    }

    val BASE_MODEL_PATH = "block/void_goop/void_goop"
    val REPLACE_TEXTURE = Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "block/crimson_sand")
    val BASE_SLOT = TextureSlot.create("0")
    val PARTICLE_SLOT = TextureSlot.create("particle")
}