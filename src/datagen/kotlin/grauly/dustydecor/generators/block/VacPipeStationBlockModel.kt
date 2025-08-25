package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.VacPipeStationBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

object VacPipeStationBlockModel {
    fun get(blockStateModelGenerator: BlockStateModelGenerator) {
        val creator = MultipartBlockModelDefinitionCreator.create(ModBlocks.VAC_PIPE_STATION)
        listOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST).forEach directionLoop@{ direction ->
            val operator = BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction]
            creator.with(
                MultipartModelConditionBuilder()
                    .put(Properties.FACING, direction),
                MODEL
                    .apply(operator)
            )
            listOf(true, false).forEach sendingLoop@{ sending ->
                creator.with(
                    MultipartModelConditionBuilder()
                        .put(Properties.FACING, direction)
                        .put(VacPipeStationBlock.SENDING, sending),
                    (if (sending) SEND_MODEL else RECEIVE_MODEL)
                        .apply(operator)
                )
            }
        }
        blockStateModelGenerator.blockStateCollector.accept(creator)
        blockStateModelGenerator.registerItemModel(ModItems.VAC_PIPE_STATION, Identifier.of(DustyDecorMod.MODID, "block/vac_pipe_station"))
    }

    private val MODEL = BlockModelDatagen.singleVariant("block/vac_pipe_station")
    private val SEND_MODEL = BlockModelDatagen.singleVariant("block/vac_pipe_station_sign_send")
    private val RECEIVE_MODEL = BlockModelDatagen.singleVariant("block/vac_pipe_station_sign_receive")
}