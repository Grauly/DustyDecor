package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.generators.block.VentBlockModel
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.render.model.json.ModelVariant
import net.minecraft.client.render.model.json.ModelVariantOperator
import net.minecraft.client.render.model.json.WeightedVariant
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.collection.Pool
import net.minecraft.util.math.AxisRotation

class BlockModelDatagen(generator: FabricDataOutput) : FabricModelProvider(generator) {

    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
        BlockDatagenWrapper.entries.filter { it.generateBlockBlockModel }
            .forEach { blockStateModelGenerator.registerSimpleCubeAll(it.block) }
        blockStateModelGenerator.registerOrientableTrapdoor(ModBlocks.VENT_COVER)
        VentBlockModel.get(blockStateModelGenerator)
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
        val NORTH_FACING_ROTATION_MAP: Map<BooleanProperty, ModelVariantOperator> = mapOf(
            Properties.UP to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R270),
            Properties.DOWN to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90),
            Properties.NORTH to ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R0),
            Properties.SOUTH to ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180),
            Properties.WEST to ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270),
            Properties.EAST to ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)
        )
    }
}