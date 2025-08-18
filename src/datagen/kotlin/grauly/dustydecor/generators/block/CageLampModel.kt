package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.LightingFixtureBlock
import grauly.dustydecor.block.TallCageLampBlock
import grauly.dustydecor.generators.BlockModelDatagen
import grauly.dustydecor.util.DyeUtils
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModels
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder
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
        createBlockModel(lamp, blockStateModelGenerator)
        createItemModel(lamp, blockStateModelGenerator)
    }

    private fun createBlockModel(lamp: TallCageLampBlock, blockStateModelGenerator: BlockStateModelGenerator) {
        val creator = MultipartBlockModelDefinitionCreator.create(lamp)
        Direction.entries.forEach { direction: Direction ->
            creator.with(
                MultipartModelConditionBuilder()
                    .put(Properties.FACING, direction),
                CAGE
                    .apply(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
            )
            creator.with(
                MultipartModelConditionBuilder()
                    .put(Properties.FACING, direction)
                    .put(LightingFixtureBlock.BROKEN, true),
                BROKEN_LAMP
                    .apply(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
            )
            listOf(true, false).forEach litLoop@{ on ->
                listOf(true, false).forEach invertedLoop@{ inverted ->
                    creator.with(
                        MultipartModelConditionBuilder()
                            .put(Properties.FACING, direction)
                            .put(LightingFixtureBlock.BROKEN, false)
                            .put(LightingFixtureBlock.LIT, on)
                            .put(LightingFixtureBlock.INVERTED, inverted),
                        (if (on != inverted) ACTIVE_LAMP else INACTIVE_LAMP)
                            .apply(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
                    )
                }
            }
        }
        blockStateModelGenerator.blockStateCollector.accept(creator)
    }

    private fun createItemModel(lamp: TallCageLampBlock, blockStateModelGenerator: BlockStateModelGenerator) {
        val color = DyeUtils.COLOR_ORDER[ModBlocks.TALL_CAGE_LAMPS.indexOf(lamp)].signColor
        val tint = ItemModels.constantTintSource(color)
        val model = ItemModels.tinted(Identifier.of(DustyDecorMod.MODID, "block/cage_lamp_tall_inventory"), tint)
        blockStateModelGenerator.itemModelOutput.accept(lamp.asItem(), model)
    }

    private val ACTIVE_LAMP = BlockModelDatagen.singleVariant("block/cage_lamp_active")
    private val INACTIVE_LAMP = BlockModelDatagen.singleVariant("block/cage_lamp_inactive")
    private val BROKEN_LAMP = BlockModelDatagen.singleVariant("block/cage_lamp_broken")
    private val CAGE = BlockModelDatagen.singleVariant("block/cage_lamp_tall_cage")
}