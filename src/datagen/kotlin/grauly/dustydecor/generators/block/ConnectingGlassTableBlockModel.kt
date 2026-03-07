package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.block.furniture.ConnectingBreakableBlock
import grauly.dustydecor.block.furniture.GranularHorizontalConnectingBlock
import grauly.dustydecor.block.furniture.HorizontalConnectingBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.ConditionBuilder
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.renderer.block.dispatch.VariantMutator
import net.minecraft.client.renderer.block.dispatch.multipart.CombinedCondition
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.core.Direction
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.state.properties.BooleanProperty
import java.util.*

open class ConnectingGlassTableBlockModel(
    blocks: List<ConnectingBreakableBlock>,
    replacements: List<Identifier>,
    basePath: String
) : ConnectingGlassTableFrameBlockModel(blocks, replacements, basePath) {
    override fun extraBlockSetup(
        block: ConnectingBreakableBlock,
        replaceTexture: Identifier,
        generator: BlockModelGenerators,
        modelGenerator: MultiPartGenerator
    ) {
        val center = replaceTexture(block, "table_top_center", replaceTexture, generator)
        val cornerInner = replaceTexture(block, "table_top_corner_inner", replaceTexture, generator)
        val cornerOuter = replaceTexture(block, "table_top_corner_outer", replaceTexture, generator)
        val edgeInner = replaceTexture(block, "table_top_edge_inner", replaceTexture, generator)
        val edgeOuter = replaceTexture(block, "table_top_edge_outer", replaceTexture, generator)

        modelGenerator.with(
            center
        )
        listOf(0, 1, 2, 3).forEach { indexOffset ->
            topEdge(
                directions[indexOffset],
                modelGenerator,
                edgeInner,
                edgeOuter
            )
            topCorner(
                directions[indexOffset],
                directions[(indexOffset + 1) % 4],
                GranularHorizontalConnectingBlock.DIRECTION_PROPERTIES[((indexOffset * 2) + 1) % 8].second,
                modelGenerator,
                cornerInner,
                cornerOuter
            )
        }
    }

    protected open fun replaceTexture(
        block: ConnectingBreakableBlock,
        targetPath: String,
        replacement: Identifier,
        generator: BlockModelGenerators
    ): MultiVariant {
        val variantModelIdentifier =
            Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "$basePath/$targetPath")
        val primarySlot = TextureSlot.create("1")
        val particleSlot = TextureSlot.create("particle")
        val material = Material(replacement)
        val mapping = TextureMapping.singleSlot(primarySlot, material).put(particleSlot, material)
        val template = ModelTemplate(Optional.of(variantModelIdentifier), Optional.empty(), primarySlot, particleSlot)
        val returnModel = template.createWithSuffix(
            block,
            "_$targetPath",
            mapping,
            generator.modelOutput
        )
        return BlockModelDatagen.singleVariant(returnModel)
    }

    protected open fun topCorner(
        direction: Direction,
        direction2: Direction,
        middle: BooleanProperty,
        modelGenerator: MultiPartGenerator,
        cornerInner: MultiVariant,
        cornerOuter: MultiVariant
    ) {
        val innerCondition = ConditionBuilder()
            .term(
                HorizontalConnectingBlock.getPropertyForDirection(direction)!!,
                HorizontalConnectingBlock.FACE_CONNECTED
            )
            .term(
                HorizontalConnectingBlock.getPropertyForDirection(direction2)!!,
                HorizontalConnectingBlock.FACE_CONNECTED
            )
            .term(middle, HorizontalConnectingBlock.FACE_CONNECTED)
        modelGenerator.with(
            innerCondition,
            cornerInner
                .with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction2]!!)
                .with(VariantMutator.UV_LOCK.withValue(true))
        )
        val outerCondition = CombinedCondition(
            CombinedCondition.Operation.OR, listOf(
                ConditionBuilder().negatedTerm(
                    HorizontalConnectingBlock.getPropertyForDirection(direction)!!,
                    HorizontalConnectingBlock.FACE_CONNECTED
                ).build(),
                ConditionBuilder().negatedTerm(
                    HorizontalConnectingBlock.getPropertyForDirection(direction2)!!,
                    HorizontalConnectingBlock.FACE_CONNECTED
                ).build(),
                ConditionBuilder().negatedTerm(middle, HorizontalConnectingBlock.FACE_CONNECTED).build(),
            )
        )
        modelGenerator.with(
            outerCondition,
            cornerOuter
                .with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction2]!!)
                .with(VariantMutator.UV_LOCK.withValue(true))
        )
    }

    protected open fun topEdge(
        direction: Direction,
        modelGenerator: MultiPartGenerator,
        edgeInner: MultiVariant,
        edgeOuter: MultiVariant
    ) {
        val property = HorizontalConnectingBlock.getPropertyForDirection(direction)!!
        listOf(true, false).forEach { state ->
            val connected = state == HorizontalConnectingBlock.FACE_CONNECTED
            modelGenerator.with(
                ConditionBuilder()
                    .term(property, connected),
                (if (connected) edgeInner else edgeOuter)
                    .with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction]!!)
                    .with(VariantMutator.UV_LOCK.withValue(true))
            )
        }
    }

    override fun createItem(
        block: ConnectingBreakableBlock,
        replaceTexture: Identifier,
        generator: BlockModelGenerators
    ) {
        val variantModelIdentifier =
            Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "$basePath/glass_table_inventory")
        val primarySlot = TextureSlot.create("1")
        val particleSlot = TextureSlot.create("particle")
        val material = Material(replaceTexture)
        val mapping = TextureMapping.singleSlot(primarySlot, material).put(particleSlot, material)
        val template = ModelTemplate(Optional.of(variantModelIdentifier), Optional.empty(), primarySlot, particleSlot)
        val tableModel = template.createWithSuffix(
            block,
            "_inventory",
            mapping,
            generator.modelOutput
        )
        generator.registerSimpleItemModel(block, tableModel)
    }
}