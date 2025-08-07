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
            makeCore(vacPipeModel, it.key)
        }
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
        aDirection: Direction
    ) {
        for(bDirection: Direction in Direction.entries) {
            creator.with(
                MultipartModelConditionBuilder()
                    .put(AbConnectableBlock.A, AbConnectableBlock.ConnectionState.fromDirection(aDirection))
                    .put(AbConnectableBlock.B, AbConnectableBlock.ConnectionState.fromDirection(bDirection)),
                if (aDirection.opposite == bDirection) VAC_CORE_STRAIGHT else VAC_CORE
            )
        }
    }

    private val VAC_CORE: WeightedVariant = BlockModelDatagen.singleVariant("block/vac_pipe_core")
    private val VAC_CONNECTOR: WeightedVariant = BlockModelDatagen.singleVariant("block/vac_pipe_connector")
    private val VAC_CORE_STRAIGHT: WeightedVariant = BlockModelDatagen.singleVariant("block/vac_pipe_core_straight")
}