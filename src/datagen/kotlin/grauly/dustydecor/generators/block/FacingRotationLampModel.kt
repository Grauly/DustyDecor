package grauly.dustydecor.generators.block

import grauly.dustydecor.block.FacingLampBlock
import grauly.dustydecor.block.FacingRotationLampBlock
import grauly.dustydecor.block.LightingFixtureBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction

class FacingRotationLampModel(lamps: List<FacingRotationLampBlock>, lampPrefix: String) :
    FacingLampModel(lamps, lampPrefix) {
    override fun createBlockModel(lamp: FacingLampBlock, blockStateModelGenerator: BlockStateModelGenerator) {
        val creator = MultipartBlockModelDefinitionCreator.create(lamp)
        Direction.entries.forEach { direction: Direction ->
            listOf(true, false).forEach { rotated ->
                creator.with(
                    MultipartModelConditionBuilder()
                        .put(Properties.FACING, direction)
                        .put(FacingRotationLampBlock.ROTATED, rotated),
                    (if (rotated) CAGE_R else CAGE)
                        .apply(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
                )
                creator.with(
                    MultipartModelConditionBuilder()
                        .put(Properties.FACING, direction)
                        .put(LightingFixtureBlock.BROKEN, true),
                    (if (rotated) BROKEN_LAMP_R else BROKEN_LAMP)
                        .apply(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
                )
                listOf(true, false).forEach litLoop@{ on ->
                    listOf(true, false).forEach invertedLoop@{ inverted ->
                        creator.with(
                            MultipartModelConditionBuilder()
                                .put(Properties.FACING, direction)
                                .put(LightingFixtureBlock.BROKEN, false)
                                .put(LightingFixtureBlock.LIT, on)
                                .put(LightingFixtureBlock.INVERTED, inverted)
                                .put(FacingRotationLampBlock.ROTATED, rotated),
                            (if (on != inverted) {
                                if (rotated) ACTIVE_LAMP_R else ACTIVE_LAMP
                            } else {
                                if (rotated) INACTIVE_LAMP_R else INACTIVE_LAMP
                            })
                                .apply(BlockModelDatagen.TOP_FACING_ROTATION_MAP[direction])
                        )
                    }
                }
            }
        }
    }

    //I hate this, but there is no better way. Why can I not just rotate around Z axis?
    protected val ACTIVE_LAMP_R = BlockModelDatagen.singleVariant("block/${lampPrefix}_active_rotated")
    protected val INACTIVE_LAMP_R = BlockModelDatagen.singleVariant("block/${lampPrefix}_inactive_rotated")
    protected val BROKEN_LAMP_R = BlockModelDatagen.singleVariant("block/${lampPrefix}_broken_rotated")
    protected val CAGE_R = BlockModelDatagen.singleVariant("block/${lampPrefix}_cage_rotated")
}