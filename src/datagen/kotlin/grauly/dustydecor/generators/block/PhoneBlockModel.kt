package grauly.dustydecor.generators.block

import com.mojang.math.Quadrant
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.furniture.PhoneBlock
import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.ConditionBuilder
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.renderer.block.dispatch.VariantMutator
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.resources.Identifier
import java.util.Optional
import kotlin.math.floor

object PhoneBlockModel {
    const val basePath = "block/phone/phone"
    private val baseModels: List<MultiVariant> = listOf(
        BlockModelDatagen.singleVariant("${basePath}_0"),
        BlockModelDatagen.singleVariant("${basePath}_1"),
        BlockModelDatagen.singleVariant("${basePath}_2"),
        BlockModelDatagen.singleVariant("${basePath}_3"),
    )
    private val brokenBaseModels = mutableListOf<MultiVariant>()
    private val handsetModels = mutableListOf(
        BlockModelDatagen.singleVariant("${basePath}_handset_0"),
        BlockModelDatagen.singleVariant("${basePath}_handset_1"),
        BlockModelDatagen.singleVariant("${basePath}_handset_2"),
        BlockModelDatagen.singleVariant("${basePath}_handset_3"),
    )
    private val brokenHandsetModels = mutableListOf<MultiVariant>()

    fun get(blockStateModelGenerator: BlockModelGenerators) {
        for (i in 0..3) {
            brokenBaseModels.add(generateBrokenModel(basePath, "$i", blockStateModelGenerator))
            brokenHandsetModels.add(generateBrokenModel(basePath, "handset_$i", blockStateModelGenerator))
        }
        getBlockModel(blockStateModelGenerator)
        getItemModel(blockStateModelGenerator)
    }

    private fun getBlockModel(blockStateModelGenerator: BlockModelGenerators) {
        val generator = MultiPartGenerator.multiPart(ModBlocks.PHONE)
        for (i in 0..15)  {
            val rotationIndex = i % 4
            val rotation = floor(i / 4.0).toInt()
            val rotationQuadrant = Quadrant.entries[rotation]
            listOf(true, false).forEach { broken ->
                generator.with(
                    ConditionBuilder()
                        .term(SingleFurnitureBlock.ROTATION, i)
                        .term(PhoneBlock.BROKEN, broken),
                    (if (broken) brokenBaseModels[rotationIndex] else baseModels[rotationIndex])
                        .with(VariantMutator.Y_ROT.withValue(rotationQuadrant))
                )
                generator.with(
                    ConditionBuilder()
                        .term(SingleFurnitureBlock.ROTATION, i)
                        .term(PhoneBlock.BROKEN, broken)
                        .term(PhoneBlock.ON_HOOK, true)
                        .term(PhoneBlock.RINGING, false),
                    (if (broken) brokenHandsetModels[rotationIndex] else handsetModels[rotationIndex])
                        .with(VariantMutator.Y_ROT.withValue(rotationQuadrant))
                )
            }
        }
        blockStateModelGenerator.blockStateOutput.accept(generator)
    }

    private fun getItemModel(blockStateModelGenerator: BlockModelGenerators) {
        val model = ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(
            DustyDecorMod.MODID,
            "${basePath}_inventory",
        ))
        blockStateModelGenerator.itemModelOutput.accept(ModBlocks.PHONE.asItem(), model)
    }

    private fun generateBrokenModel(path: String, suffix: String, blockStateModelGenerator: BlockModelGenerators): MultiVariant {
        val sourceModelIdentifier = Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "${path}_$suffix")
        val slot0 = TextureSlot.create("0")
        val slot1 = TextureSlot.create("1")
        val particleSlot = TextureSlot.create("particle")
        val materialBase = Material(Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "block/phone_base_broken"))
        val materialHandlings = Material(Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "block/phone_handlings_broken"))
        val mapping = TextureMapping
            .singleSlot(slot0, materialBase)
            .put(slot1, materialHandlings)
            .put(particleSlot, materialBase)
        val template = ModelTemplate(Optional.of(sourceModelIdentifier), Optional.empty(), slot0, slot1, particleSlot)
        val returnModel = template.createWithSuffix(ModBlocks.PHONE, "_broken_$suffix", mapping, blockStateModelGenerator.modelOutput)
        return BlockModelDatagen.singleVariant(returnModel)
    }

}