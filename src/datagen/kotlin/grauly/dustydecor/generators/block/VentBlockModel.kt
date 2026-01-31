package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.vent.SideConnectableBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.client.renderer.block.model.VariantMutator
import net.minecraft.client.data.models.blockstates.ConditionBuilder
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.resources.Identifier

object VentBlockModel {
    fun get(blockStateModelGenerator: BlockModelGenerators) {
        val ventModel = MultiPartGenerator.multiPart(ModBlocks.VENT)
            .with(VENT_CORE)

        BlockModelDatagen.NORTH_FACING_ROTATION_MAP.forEach { (direction, operator) ->
            ventSideModel(ventModel, BlockModelDatagen.DIRECTION_TO_PROPERTY_MAP[direction]!!, operator)
        }

        blockStateModelGenerator.blockStateOutput?.accept(ventModel)
        blockStateModelGenerator.registerSimpleItemModel(
            ModItems.VENT,
            Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "block/vent/vent_inventory")
        )
    }

    private fun ventSideModel(
        creator: MultiPartGenerator,
        direction: Property<Boolean>,
        operator: VariantMutator
    ) {
        creator.with(
            ConditionBuilder().term(direction, !SideConnectableBlock.FACE_CONNECTED),
            VENT_COVER
                .with(VariantMutator.UV_LOCK.withValue(true))
                .with(operator)
        )
    }

    private val VENT_COVER: MultiVariant = BlockModelDatagen.singleVariant("block/vent/vent_cover")
    private val VENT_CORE: MultiVariant = BlockModelDatagen.singleVariant("block/vent/vent_core")
}