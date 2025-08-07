package grauly.dustydecor.generators.block

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.AbConnectableBlock
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
        AbConnectableBlock.ConnectionState.entries.forEach { makeCore(vacPipeModel, it) }
        blockStateModelGenerator.blockStateCollector?.accept(vacPipeModel)
    }

    private fun singleConnectorRotation(
        creator: MultipartBlockModelDefinitionCreator,
        direction: Direction,
        operator: ModelVariantOperator
    ) {
        listOf(AbConnectableBlock.A, AbConnectableBlock.B).forEach {
            creator.with(
                MultipartModelConditionBuilder().put(it, AbConnectableBlock.ConnectionState.fromDirection(direction)),
                VAC_CONNECTOR
                    .apply(ModelVariantOperator.UV_LOCK.withValue(true))
                    .apply(operator)
            )
        }
    }

    private fun makeCore(
        creator: MultipartBlockModelDefinitionCreator,
        aState: AbConnectableBlock.ConnectionState
    ) {
        for(bState: AbConnectableBlock.ConnectionState in AbConnectableBlock.ConnectionState.entries) {
            creator.with(
                MultipartModelConditionBuilder()
                    .put(AbConnectableBlock.A, aState)
                    .put(AbConnectableBlock.B, bState),
                if (aState == AbConnectableBlock.ConnectionState.NONE || bState == AbConnectableBlock.ConnectionState.NONE) {
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