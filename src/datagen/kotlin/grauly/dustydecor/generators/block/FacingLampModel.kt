package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.block.lamp.LightingFixtureBlock
import grauly.dustydecor.block.lamp.FacingLampBlock
import grauly.dustydecor.generators.BlockModelDatagen
import grauly.dustydecor.util.DyeUtils
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.client.data.models.blockstates.ConditionBuilder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.Direction

open class FacingLampModel(private val lamps: List<FacingLampBlock>, private val lampPrefix: String) {
    fun get(blockStateModelGenerator: BlockModelGenerators) {
        lamps.forEach {
            createCageLamp(it, blockStateModelGenerator)
        }
    }

    private fun createCageLamp(lamp: FacingLampBlock, blockStateModelGenerator: BlockModelGenerators) {
        createBlockModel(lamp, blockStateModelGenerator)
        createItemModel(lamp, blockStateModelGenerator)
    }

    protected open fun createBlockModel(lamp: FacingLampBlock, blockStateModelGenerator: BlockModelGenerators) {
        val creator = MultiPartGenerator.multiPart(lamp)
        Direction.entries.forEach { direction: Direction ->
            creator.with(
                ConditionBuilder()
                    .term(BlockStateProperties.FACING, direction),
                CAGE
                    .with(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
            )
            creator.with(
                ConditionBuilder()
                    .term(BlockStateProperties.FACING, direction)
                    .term(LightingFixtureBlock.BROKEN, true),
                BROKEN_LAMP
                    .with(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
            )
            listOf(true, false).forEach litLoop@{ on ->
                listOf(true, false).forEach invertedLoop@{ inverted ->
                    creator.with(
                        ConditionBuilder()
                            .term(BlockStateProperties.FACING, direction)
                            .term(LightingFixtureBlock.BROKEN, false)
                            .term(LightingFixtureBlock.LIT, on)
                            .term(LightingFixtureBlock.INVERTED, inverted),
                        (if (on != inverted) ACTIVE_LAMP else INACTIVE_LAMP)
                            .with(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
                    )
                }
            }
        }
        blockStateModelGenerator.blockStateOutput.accept(creator)
    }

    protected open fun createItemModel(lamp: FacingLampBlock, blockStateModelGenerator: BlockModelGenerators) {
        val color = DyeUtils.COLOR_ORDER[lamps.indexOf(lamp)].textColor
        val tint = ItemModelUtils.constantTint(color)
        val model = ItemModelUtils.tintedModel(ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, "block/${lampPrefix}_inventory"), tint)
        blockStateModelGenerator.itemModelOutput.accept(lamp.asItem(), model)
    }

    protected val ACTIVE_LAMP = BlockModelDatagen.singleVariant("block/${lampPrefix}_active")
    protected val INACTIVE_LAMP = BlockModelDatagen.singleVariant("block/${lampPrefix}_inactive")
    protected val BROKEN_LAMP = BlockModelDatagen.singleVariant("block/${lampPrefix}_broken")
    protected val CAGE = BlockModelDatagen.singleVariant("block/${lampPrefix}_cage")
}