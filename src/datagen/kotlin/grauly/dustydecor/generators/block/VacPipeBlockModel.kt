package grauly.dustydecor.generators.block

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.NConnectableBlock
import grauly.dustydecor.block.VacPipeBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator
import net.minecraft.client.render.model.json.ModelVariantOperator
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder
import net.minecraft.client.render.model.json.WeightedVariant
import net.minecraft.util.math.Direction

object VacPipeBlockModel {
    fun get(blockStateModelGenerator: BlockStateModelGenerator) {
        val vacPipeModel = MultipartBlockModelDefinitionCreator.create(ModBlocks.VAC_PIPE)
        BlockModelDatagen.NORTH_FACING_ROTATION_MAP.forEach {
            singleConnectorRotation(vacPipeModel, it.key, it.value)
        }
        NConnectableBlock.ConnectionState.entries.forEach { makeCore(vacPipeModel, it) }
        blockStateModelGenerator.blockStateCollector?.accept(vacPipeModel)
    }

    private fun singleConnectorRotation(
        creator: MultipartBlockModelDefinitionCreator,
        direction: Direction,
        operator: ModelVariantOperator
    ) {
        (ModBlocks.VAC_PIPE as VacPipeBlock).connections.forEach {
            creator.with(
                MultipartModelConditionBuilder().put(it, NConnectableBlock.ConnectionState.fromDirection(direction)),
                VAC_CONNECTOR
                    .apply(ModelVariantOperator.UV_LOCK.withValue(true))
                    .apply(operator)
            )
        }
    }

    private fun makeCore(
        creator: MultipartBlockModelDefinitionCreator,
        aState: NConnectableBlock.ConnectionState
    ) {
        val connections = (ModBlocks.VAC_PIPE as VacPipeBlock).connections
        for (bState: NConnectableBlock.ConnectionState in NConnectableBlock.ConnectionState.entries) {
            creator.with(
                MultipartModelConditionBuilder()
                    .put(connections[0], aState)
                    .put(connections[1], bState),
                if (aState == NConnectableBlock.ConnectionState.NONE || bState == NConnectableBlock.ConnectionState.NONE) {
                    VAC_CORE
                } else if (aState.direction?.opposite!! == bState.direction!!) {
                    VAC_CORE_STRAIGHT
                        .apply(ModelVariantOperator.UV_LOCK.withValue(true))
                        .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aState.direction])
                } else {
                    VAC_CORE
                }
            )
        }
    }

    private val VAC_CORE: WeightedVariant = BlockModelDatagen.singleVariant("block/vac_pipe_core")
    private val VAC_CONNECTOR: WeightedVariant = BlockModelDatagen.singleVariant("block/vac_pipe_connector")
    private val VAC_CORE_STRAIGHT: WeightedVariant = BlockModelDatagen.singleVariant("block/vac_pipe_core_straight")
}