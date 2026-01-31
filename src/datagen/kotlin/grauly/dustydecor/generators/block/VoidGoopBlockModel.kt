package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.resources.Identifier

object VoidGoopBlockModel {
    fun get(blockStateModelGenerator: BlockModelGenerators) {
        val creator = MultiVariantGenerator.dispatch(ModBlocks.VOID_GOOP)
        val layersMap = PropertyDispatch.initial(BlockStateProperties.LAYERS).generate { layers ->
            BlockModelDatagen.singleVariant("${LAYER_BASE_PATH}_$layers")
        }
        blockStateModelGenerator.blockStateOutput.accept(creator.with(layersMap))
        blockStateModelGenerator.registerSimpleFlatItemModel(ModItems.VOID_GOOP)
    }

    private const val LAYER_BASE_PATH = "block/void_goop/void_goop"
}