package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.BlockStateVariantMap
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier

object VoidGoopBlockModel {
    fun get(blockStateModelGenerator: BlockStateModelGenerator) {
        val creator = VariantsBlockModelDefinitionCreator.of(ModBlocks.VOID_GOOP)
        val layersMap = BlockStateVariantMap.models(Properties.LAYERS).generate { layers ->
            BlockModelDatagen.singleVariant("${LAYER_BASE_PATH}_$layers")
        }
        blockStateModelGenerator.blockStateCollector.accept(creator.with(layersMap))
        blockStateModelGenerator.registerItemModel(ModItems.VOID_GOOP)
    }

    private const val LAYER_BASE_PATH = "block/void_goop/void_goop"
}