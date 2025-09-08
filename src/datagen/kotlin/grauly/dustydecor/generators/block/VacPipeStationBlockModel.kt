package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator
import net.minecraft.util.Identifier

object VacPipeStationBlockModel {
    fun get(blockStateModelGenerator: BlockStateModelGenerator) {
        val creator = VariantsBlockModelDefinitionCreator.of(ModBlocks.VAC_PIPE_STATION, MODEL)
        blockStateModelGenerator.blockStateCollector.accept(creator)
        blockStateModelGenerator.registerItemModel(ModItems.VAC_PIPE_STATION, Identifier.of(DustyDecorMod.MODID, "block/vac_pipe_station/vac_pipe_station"))
    }

    private val MODEL = BlockModelDatagen.singleVariant("block/vac_pipe_station/vac_pipe_station")
}