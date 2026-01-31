package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.resources.Identifier

object VacPipeStationBlockModel {
    fun get(blockStateModelGenerator: BlockModelGenerators) {
        val creator = MultiVariantGenerator.dispatch(ModBlocks.VAC_PIPE_STATION, MODEL)
        blockStateModelGenerator.blockStateOutput.accept(creator)
        blockStateModelGenerator.registerSimpleItemModel(ModItems.VAC_PIPE_STATION, Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "block/vac_pipe_station/vac_pipe_station"))
    }

    private val MODEL = BlockModelDatagen.singleVariant("block/vac_pipe_station/vac_pipe_station")
}