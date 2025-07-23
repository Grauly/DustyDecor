package grauly.dustydecor

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator
import net.minecraft.client.render.model.json.ModelVariant
import net.minecraft.client.render.model.json.ModelVariantOperator
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder
import net.minecraft.client.render.model.json.WeightedVariant
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.state.property.Property
import net.minecraft.util.Identifier
import net.minecraft.util.collection.Pool
import net.minecraft.util.math.AxisRotation

class ModBlockModelDatagen(generator: FabricDataOutput) : FabricModelProvider(generator) {

    private val VENT_COVER: WeightedVariant = WeightedVariant(
        Pool.of(
            ModelVariant(
                Identifier.of(DustyDecorMod.MODID, "block/vent_cover"),
                ModelVariant.ModelState.DEFAULT
            )
        )
    )
    private val VENT_CORE: WeightedVariant = WeightedVariant(
        Pool.of(
            ModelVariant(
                Identifier.of(DustyDecorMod.MODID, "block/vent_core"),
                ModelVariant.ModelState.DEFAULT
            )
        )
    )
    private val NORTH_FACING_ROTATION_MAP: Map<BooleanProperty, ModelVariantOperator> = mapOf(
        Properties.UP to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R270),
        Properties.DOWN to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90),
        Properties.NORTH to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R0),
        Properties.SOUTH to ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180),
        Properties.WEST to ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270),
        Properties.EAST to ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)
    )

    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
        val ventModel = MultipartBlockModelDefinitionCreator.create(ModBlocks.VENT)
            .with(VENT_CORE)

        NORTH_FACING_ROTATION_MAP.forEach { (direction, operator) ->
            ventSideModel(ventModel, direction, operator)
        }

        blockStateModelGenerator.blockStateCollector?.accept(ventModel)
        blockStateModelGenerator.registerItemModel(ModItems.VENT_ITEM, blockStateModelGenerator.uploadBlockItemModel(ModItems.VENT_ITEM, ModBlocks.VENT))
    }

    private fun ventSideModel(
        creator: MultipartBlockModelDefinitionCreator,
        direction: Property<Boolean>,
        operator: ModelVariantOperator
    ) {
        creator.with(
            MultipartModelConditionBuilder().put(direction, true),
            VENT_COVER
                .apply(ModelVariantOperator.UV_LOCK.withValue(true))
                .apply(operator)
        )
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        //[Intentionally Left Blank]
    }

    override fun getName(): String = "Block Model Definitions"
}