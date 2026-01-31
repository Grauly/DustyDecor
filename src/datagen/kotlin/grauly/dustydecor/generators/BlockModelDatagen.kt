package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.generators.block.*
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.renderer.block.model.Variant
import net.minecraft.client.renderer.block.model.VariantMutator
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.random.WeightedList
import com.mojang.math.Quadrant
import net.minecraft.core.Direction

class BlockModelDatagen(generator: FabricDataOutput) : FabricModelProvider(generator) {

    override fun generateBlockStateModels(blockStateModelGenerator: BlockModelGenerators) {
        BlockDatagenWrapper.entries.filter { it.generateCubeAllModel }
            .forEach { blockStateModelGenerator.createTrivialCube(it.block) }
        blockStateModelGenerator.createOrientableTrapdoor(ModBlocks.VENT_COVER)
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


    override fun generateItemModels(itemModelGenerator: ItemModelGenerators) {
        //[Intentionally Left Blank]
    }

    override fun getName(): String = "Block Model Definitions"

    companion object {
        fun singleVariant(identifier: ResourceLocation): MultiVariant =
            MultiVariant(
                WeightedList.of(
                    Variant(
                        identifier,
                        Variant.SimpleModelState.DEFAULT
                    )
                )
            )

        fun singleVariant(id: String) = singleVariant(ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, id))
        val NORTH_FACING_ROTATION_MAP: Map<Direction, VariantMutator> = mapOf(
            Direction.UP to VariantMutator.X_ROT.withValue(Quadrant.R270),
            Direction.DOWN to VariantMutator.X_ROT.withValue(Quadrant.R90),
            Direction.NORTH to VariantMutator.X_ROT.withValue(Quadrant.R0),
            Direction.SOUTH to VariantMutator.Y_ROT.withValue(Quadrant.R180),
            Direction.WEST to VariantMutator.Y_ROT.withValue(Quadrant.R270),
            Direction.EAST to VariantMutator.Y_ROT.withValue(Quadrant.R90)
        )
        val TOP_FACING_ROTATION_MAP: Map<Direction, VariantMutator> = mapOf(
            Direction.UP to VariantMutator.X_ROT.withValue(Quadrant.R0),
            Direction.DOWN to VariantMutator.X_ROT.withValue(Quadrant.R180),
            Direction.NORTH to VariantMutator.X_ROT.withValue(Quadrant.R90),
            Direction.SOUTH to VariantMutator.X_ROT.withValue(Quadrant.R270),
            Direction.WEST to VariantMutator.X_ROT.withValue(Quadrant.R90)
                .then(VariantMutator.Y_ROT.withValue(Quadrant.R270)),
            Direction.EAST to VariantMutator.X_ROT.withValue(Quadrant.R90)
                .then(VariantMutator.Y_ROT.withValue(Quadrant.R90))
        )
        val DIRECTION_TO_PROPERTY_MAP: Map<Direction, Property<Boolean>> = mapOf(
            Direction.UP to BlockStateProperties.UP,
            Direction.DOWN to BlockStateProperties.DOWN,
            Direction.NORTH to BlockStateProperties.NORTH,
            Direction.SOUTH to BlockStateProperties.SOUTH,
            Direction.WEST to BlockStateProperties.WEST,
            Direction.EAST to BlockStateProperties.EAST
        )
    }
}