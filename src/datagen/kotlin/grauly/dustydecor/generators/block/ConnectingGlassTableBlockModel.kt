package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.block.furniture.ConnectingGlassTableBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.blockstates.ConditionBuilder
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.renderer.block.model.Material
import net.minecraft.resources.Identifier
import org.w3c.dom.Text
import java.util.*

class ConnectingGlassTableBlockModel(
    blocks: List<ConnectingGlassTableBlock>,
    replacements: List<Identifier>,
    basePath: String
) : ConnectingGlassTableFrameBlockModel(blocks, replacements, basePath) {
    override fun extraBlockSetup(
        block: ConnectingGlassTableBlock,
        replaceTexture: Identifier,
        generator: BlockModelGenerators,
        modelGenerator: MultiPartGenerator
    ) {
        val variantModelIdentifier = Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "$basePath/table_top")
        val primarySlot = TextureSlot.create("1")
        val particleSlot = TextureSlot.create("particle")
        val material = Material(replaceTexture)
        val mapping = TextureMapping.singleSlot(primarySlot, material).put(particleSlot, material)
        val template = ModelTemplate(Optional.of(variantModelIdentifier), Optional.empty(), primarySlot, particleSlot)
        val paneModel = BlockModelDatagen.singleVariant(
            template.createWithSuffix(
                block,
                "_table_top",
                mapping,
                generator.modelOutput
            )
        )

        modelGenerator.with(
            ConditionBuilder().term(ConnectingGlassTableBlock.BROKEN, false),
            paneModel,
        )
    }

    override fun createItem(
        block: ConnectingGlassTableBlock,
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