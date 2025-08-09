package grauly.dustydecor.generators.block

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.AbConnectableBlock
import grauly.dustydecor.block.ConnectionState
import grauly.dustydecor.block.VacPipeBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator
import net.minecraft.client.render.model.json.*
import net.minecraft.util.math.Direction

object VacPipeBlockModel {
    fun get(blockStateModelGenerator: BlockStateModelGenerator) {
        val vacPipeModel = MultipartBlockModelDefinitionCreator.create(ModBlocks.VAC_PIPE)
        BlockModelDatagen.NORTH_FACING_ROTATION_MAP.forEach {
            singleConnectorRotation(vacPipeModel, it.key, it.value)
        }
        ConnectionState.entries.forEach { makeCore(vacPipeModel, it) }
        blockStateModelGenerator.blockStateCollector?.accept(vacPipeModel)
    }

    private fun singleConnectorRotation(
        creator: MultipartBlockModelDefinitionCreator,
        direction: Direction,
        operator: ModelVariantOperator
    ) {
        AbConnectableBlock.connections.forEach {
            //Base, full opaque
            creator.with(
                MultipartModelConditionBuilder()
                    .put(it, ConnectionState.fromDirection(direction))
                    .put(VacPipeBlock.windowMap[it], false),
                VAC_CONNECTOR
                    .apply(ModelVariantOperator.UV_LOCK.withValue(false))
                    .apply(operator)
            )

            //Transparent Variant
            val transparentCondition = MultipartModelConditionBuilder()
                .put(it, ConnectionState.fromDirection(direction))
            AbConnectableBlock.connections.forEach { f -> transparentCondition.put(VacPipeBlock.windowMap[f], true) }
            creator.with(
                transparentCondition,
                VAC_CONNECTOR_TRANSPARENT
                    .apply(ModelVariantOperator.UV_LOCK.withValue(false))
                    .apply(operator)
            )

            //Window Variant
            val orList: List<MultipartModelCondition> = AbConnectableBlock.connections.filter { f -> f != it }
                .map { f -> MultipartModelConditionBuilder().put(VacPipeBlock.windowMap[f], false).build() }
            val directionCondition = MultipartModelConditionBuilder()
                .put(it, ConnectionState.fromDirection(direction))
                .put(VacPipeBlock.windowMap[it], true)
                .build()
            val anyNotWindow =
                MultipartModelCombinedCondition(MultipartModelCombinedCondition.LogicalOperator.OR, orList)
            val windowCondition = MultipartModelCombinedCondition(
                MultipartModelCombinedCondition.LogicalOperator.AND,
                listOf(directionCondition, anyNotWindow)
            )
            creator.with(
                windowCondition,
                VAC_CONNECTOR_WINDOW
                    .apply(ModelVariantOperator.UV_LOCK.withValue(false))
                    .apply(operator)
            )
        }
    }

    private fun makeCore(
        creator: MultipartBlockModelDefinitionCreator,
        aState: ConnectionState
    ) {
        val connections = AbConnectableBlock.connections

        //Base, full opaque
        val falseOrList: List<MultipartModelCondition> = AbConnectableBlock.connections.map { f ->
            MultipartModelConditionBuilder().put(
                VacPipeBlock.windowMap[f],
                false
            ).build()
        }
        val anyNotWindowCondition =
            MultipartModelCombinedCondition(MultipartModelCombinedCondition.LogicalOperator.OR, falseOrList)
        for (bState: ConnectionState in ConnectionState.entries) {
            creator.with(
                MultipartModelCombinedCondition(
                    MultipartModelCombinedCondition.LogicalOperator.AND, listOf(
                        MultipartModelConditionBuilder()
                            .put(connections[0], aState)
                            .put(connections[1], bState)
                            .build(),
                        anyNotWindowCondition
                    )
                ),
                VAC_CORE
            )
        }

        //Window and transparent, aka transparent, but around corner
        val trueAndList: List<MultipartModelCondition> = AbConnectableBlock.connections.map { f ->
            MultipartModelConditionBuilder().put(VacPipeBlock.windowMap[f], true).build()
        }
        val allWindowCondition =
            MultipartModelCombinedCondition(MultipartModelCombinedCondition.LogicalOperator.AND, trueAndList)
        for (bState: ConnectionState in ConnectionState.entries) {
            creator.with(
                MultipartModelCombinedCondition(
                    MultipartModelCombinedCondition.LogicalOperator.AND, listOf(
                        MultipartModelConditionBuilder()
                            .put(connections[0], aState)
                            .put(connections[1], bState)
                            .build(),
                        allWindowCondition
                    )
                ),
                run {
                    if (aState != ConnectionState.NONE && bState != ConnectionState.NONE) {
                        if (aState.direction!!.opposite == bState.direction) {
                            return@run VAC_CORE_TRANSPARENT
                                .apply(ModelVariantOperator.UV_LOCK.withValue(false))
                                .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aState.direction])
                        }
                    }
                    return@run VAC_CORE_WINDOW
                }
            )
        }
    }

    private val VAC_CORE: WeightedVariant = BlockModelDatagen.singleVariant("block/vac_pipe_core")
    private val VAC_CORE_WINDOW: WeightedVariant = BlockModelDatagen.singleVariant("block/vac_pipe_core_window")
    private val VAC_CORE_TRANSPARENT: WeightedVariant =
        BlockModelDatagen.singleVariant("block/vac_pipe_core_transparent")
    private val VAC_CONNECTOR: WeightedVariant = BlockModelDatagen.singleVariant("block/vac_pipe_connector")
    private val VAC_CONNECTOR_WINDOW: WeightedVariant =
        BlockModelDatagen.singleVariant("block/vac_pipe_connector_window")
    private val VAC_CONNECTOR_TRANSPARENT: WeightedVariant =
        BlockModelDatagen.singleVariant("block/vac_pipe_connector_transparent")
}