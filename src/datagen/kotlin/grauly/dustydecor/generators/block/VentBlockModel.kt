package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.SideConnectableBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator
import net.minecraft.client.render.model.json.ModelVariantOperator
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder
import net.minecraft.client.render.model.json.WeightedVariant
import net.minecraft.state.property.Property
import net.minecraft.util.Identifier

object VentBlockModel {
    fun get(blockStateModelGenerator: BlockStateModelGenerator) {
        val ventModel = MultipartBlockModelDefinitionCreator.create(ModBlocks.VENT)
            .with(VENT_CORE)

        BlockModelDatagen.NORTH_FACING_ROTATION_MAP.forEach { (direction, operator) ->
            ventSideModel(ventModel, BlockModelDatagen.DIRECTION_TO_PROPERTY_MAP[direction]!!, operator)
        }

        blockStateModelGenerator.blockStateCollector?.accept(ventModel)
        blockStateModelGenerator.registerItemModel(
            ModItems.VENT,
            Identifier.of(DustyDecorMod.MODID, "block/vent_inventory")
        )
    }

    private fun ventSideModel(
        creator: MultipartBlockModelDefinitionCreator,
        direction: Property<Boolean>,
        operator: ModelVariantOperator
    ) {
        creator.with(
            MultipartModelConditionBuilder().put(direction, !SideConnectableBlock.FACE_CONNECTED),
            VENT_COVER
                .apply(ModelVariantOperator.UV_LOCK.withValue(true))
                .apply(operator)
        )
    }

    private val VENT_COVER: WeightedVariant = BlockModelDatagen.singleVariant("block/vent_cover")
    private val VENT_CORE: WeightedVariant = BlockModelDatagen.singleVariant("block/vent_core")
}