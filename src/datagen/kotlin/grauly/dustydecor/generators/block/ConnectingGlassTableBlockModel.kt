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
import java.util.Optional

class ConnectingGlassTableBlockModel(
    blocks: List<ConnectingGlassTableBlock>,
    replacements: List<Identifier>,
    basePath: String
) : ConnectingGlassTableFrameBlockModel(blocks, replacements, basePath) {
    override fun extraSetup(
        block: ConnectingGlassTableBlock,
        replaceTexture: Identifier,
        generator: BlockModelGenerators,
        modelGenerator: MultiPartGenerator
    ) {
        val variantModelIdentifier = Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "$basePath/table_top")
        val slot = TextureSlot.create("0")
        val mapping = TextureMapping.singleSlot(slot, Material(replaceTexture))
        val template = ModelTemplate(Optional.of(variantModelIdentifier), Optional.empty(), slot)
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
}