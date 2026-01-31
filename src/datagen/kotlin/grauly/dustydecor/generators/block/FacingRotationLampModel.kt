package grauly.dustydecor.generators.block

import grauly.dustydecor.block.lamp.FacingLampBlock
import grauly.dustydecor.block.lamp.FacingRotationLampBlock
import grauly.dustydecor.block.lamp.LightingFixtureBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.client.data.models.blockstates.ConditionBuilder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction

class FacingRotationLampModel(lamps: List<FacingRotationLampBlock>, lampPrefix: String) :
    FacingLampModel(lamps, lampPrefix) {
    override fun createBlockModel(lamp: FacingLampBlock, blockStateModelGenerator: BlockModelGenerators) {
        val creator = MultiPartGenerator.multiPart(lamp)
        Direction.entries.forEach { direction: Direction ->
            listOf(true, false).forEach { rotated ->
                creator.with(
                    ConditionBuilder()
                        .term(BlockStateProperties.FACING, direction)
                        .term(FacingRotationLampBlock.ROTATED, rotated),
                    (if (rotated) CAGE_R else CAGE)
                        .with(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
                )
                creator.with(
                    ConditionBuilder()
                        .term(BlockStateProperties.FACING, direction)
                        .term(LightingFixtureBlock.BROKEN, true)
                        .term(FacingRotationLampBlock.ROTATED, rotated),
                    (if (rotated) BROKEN_LAMP_R else BROKEN_LAMP)
                        .with(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
                )
                listOf(true, false).forEach litLoop@{ on ->
                    listOf(true, false).forEach invertedLoop@{ inverted ->
                        creator.with(
                            ConditionBuilder()
                                .term(BlockStateProperties.FACING, direction)
                                .term(LightingFixtureBlock.BROKEN, false)
                                .term(LightingFixtureBlock.LIT, on)
                                .term(LightingFixtureBlock.INVERTED, inverted)
                                .term(FacingRotationLampBlock.ROTATED, rotated),
                            (if (on != inverted) {
                                if (rotated) ACTIVE_LAMP_R else ACTIVE_LAMP
                            } else {
                                if (rotated) INACTIVE_LAMP_R else INACTIVE_LAMP
                            })
                                .with(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
                        )
                    }
                }
            }
        }
        blockStateModelGenerator.blockStateOutput.accept(creator)
    }

    //I hate this, but there is no better way. Why can I not just rotate around Z axis?
    protected val ACTIVE_LAMP_R = BlockModelDatagen.singleVariant("block/${lampPrefix}_active_rotated")
    protected val INACTIVE_LAMP_R = BlockModelDatagen.singleVariant("block/${lampPrefix}_inactive_rotated")
    protected val BROKEN_LAMP_R = BlockModelDatagen.singleVariant("block/${lampPrefix}_broken_rotated")
    protected val CAGE_R = BlockModelDatagen.singleVariant("block/${lampPrefix}_cage_rotated")
}