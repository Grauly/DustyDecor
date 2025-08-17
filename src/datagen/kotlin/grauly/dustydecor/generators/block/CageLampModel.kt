package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.TallCageLampBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.BlockStateVariantMap
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

object CageLampModel {
    fun get(blockStateModelGenerator: BlockStateModelGenerator) {
        ModBlocks.TALL_CAGE_LAMPS.forEach {
            createCageLamp(it, blockStateModelGenerator)
        }
    }

    private fun createCageLamp(lamp: TallCageLampBlock, blockStateModelGenerator: BlockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator
            .of(
                lamp,
                BlockModelDatagen.singleVariant("block/cage_lamp_tall")
            ).coordinate(facingRotator))
        blockStateModelGenerator.registerItemModel(lamp.asItem(), Identifier.of(DustyDecorMod.MODID, "block/cage_lamp_tall"))
    }

    private val facingRotator = Direction.entries.fold(BlockStateVariantMap.operations(Properties.FACING))
    {
        builder, element -> builder.register(element, BlockModelDatagen.NORTH_FACING_ROTATION_MAP[element])
    }
}