package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.generators.block.*
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.render.model.json.ModelVariant
import net.minecraft.client.render.model.json.ModelVariantOperator
import net.minecraft.client.render.model.json.WeightedVariant
import net.minecraft.state.property.Properties
import net.minecraft.state.property.Property
import net.minecraft.util.Identifier
import net.minecraft.util.collection.Pool
import net.minecraft.util.math.AxisRotation
import net.minecraft.util.math.Direction

class BlockModelDatagen(generator: FabricDataOutput) : FabricModelProvider(generator) {

    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
        BlockDatagenWrapper.entries.filter { it.generateCubeAllModel }
            .forEach { blockStateModelGenerator.registerSimpleCubeAll(it.block) }
        blockStateModelGenerator.registerOrientableTrapdoor(ModBlocks.VENT_COVER)
        VentBlockModel.get(blockStateModelGenerator)
        VacPipeBlockModel.get(blockStateModelGenerator)
        FacingLampModel(ModBlocks.TALL_CAGE_LAMPS,"cage_lamp_tall/cage_lamp_tall").get(blockStateModelGenerator)
        FacingLampModel(ModBlocks.ALARM_CAGE_LAMPS,"cage_lamp_tall/cage_lamp_tall").get(blockStateModelGenerator)
        FacingRotationLampModel(ModBlocks.WIDE_CAGE_LAMPS,"cage_lamp_wide/cage_lamp_wide").get(blockStateModelGenerator)
        FacingRotationLampModel(ModBlocks.TUBE_LAMPS,"tube_lamp/tube_lamp").get(blockStateModelGenerator)
        VacPipeStationBlockModel.get(blockStateModelGenerator)
        VoidGoopBlockModel.get(blockStateModelGenerator)
        SingleFurnitureBlockModel(ModBlocks.STOOLS, "stool/stool").get(blockStateModelGenerator)
    }


    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        //[Intentionally Left Blank]
    }

    override fun getName(): String = "Block Model Definitions"

    companion object {
        fun singleVariant(identifier: Identifier): WeightedVariant =
            WeightedVariant(
                Pool.of(
                    ModelVariant(
                        identifier,
                        ModelVariant.ModelState.DEFAULT
                    )
                )
            )

        fun singleVariant(id: String) = singleVariant(Identifier.of(DustyDecorMod.MODID, id))
        val NORTH_FACING_ROTATION_MAP: Map<Direction, ModelVariantOperator> = mapOf(
            Direction.UP to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R270),
            Direction.DOWN to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90),
            Direction.NORTH to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R0),
            Direction.SOUTH to ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180),
            Direction.WEST to ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270),
            Direction.EAST to ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)
        )
        val TOP_FACING_ROTATION_MAP: Map<Direction, ModelVariantOperator> = mapOf(
            Direction.UP to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R0),
            Direction.DOWN to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R180),
            Direction.NORTH to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90),
            Direction.SOUTH to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R270),
            Direction.WEST to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)
                .then(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)),
            Direction.EAST to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)
                .then(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
        )
        val DIRECTION_TO_PROPERTY_MAP: Map<Direction, Property<Boolean>> = mapOf(
            Direction.UP to Properties.UP,
            Direction.DOWN to Properties.DOWN,
            Direction.NORTH to Properties.NORTH,
            Direction.SOUTH to Properties.SOUTH,
            Direction.WEST to Properties.WEST,
            Direction.EAST to Properties.EAST
        )
    }
}