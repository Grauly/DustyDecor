package grauly.dustydecor.generators

import grauly.dustydecor.ModItems
import grauly.dustydecor.util.DyeUtils
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class RecipeDatagen(
    output: FabricDataOutput?,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>?,
) : FabricRecipeProvider(output, registriesFuture) {
    override fun getName(): String {
        return "Recipe Provider"
    }

    override fun getRecipeGenerator(wrapper: RegistryWrapper.WrapperLookup, exporter: RecipeExporter): RecipeGenerator {
        return object : RecipeGenerator(wrapper, exporter) {
            override fun generate() {
                createShapeless(RecipeCategory.DECORATIONS, ModItems.VENT_COVER, 2)
                    .input(Items.IRON_TRAPDOOR)
                    .criterion("has_iron", conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                    .offerTo(exporter)
                createShapeless(RecipeCategory.DECORATIONS, Items.IRON_TRAPDOOR)
                    .input(ModItems.VENT_COVER, 2)
                    .criterion(hasItem(Items.IRON_TRAPDOOR), conditionsFromItem(Items.IRON_TRAPDOOR))
                    .offerTo(exporter)
                createShaped(RecipeCategory.DECORATIONS, ModItems.VENT, 16)
                    .input('c', Items.IRON_TRAPDOOR)
                    .pattern("ccc")
                    .pattern("c c")
                    .pattern("ccc")
                    .criterion("has_iron", conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                    .offerTo(exporter)
                createShaped(RecipeCategory.TOOLS, ModItems.SCREWDRIVER)
                    .input('i', ConventionalItemTags.IRON_INGOTS)
                    .input('r', ConventionalItemTags.RED_DYES)
                    .pattern(" i")
                    .pattern("r ")
                    .criterion("has_iron", conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                    .offerTo(exporter)
                createShaped(RecipeCategory.TOOLS, ModItems.WRENCH)
                    .input('i', ConventionalItemTags.IRON_INGOTS)
                    .input('b', ConventionalItemTags.BLUE_DYES)
                    .input('t', Items.IRON_TRAPDOOR)
                    .pattern(" ti")
                    .pattern("ti ")
                    .pattern("b  ")
                    .criterion("has_iron", conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                    .offerTo(exporter)
                createShaped(RecipeCategory.REDSTONE, ModItems.VAC_PIPE, 6)
                    .input('c', ConventionalItemTags.COPPER_INGOTS)
                    .input('g', ConventionalItemTags.GLASS_PANES_COLORLESS)
                    .pattern("ccc")
                    .pattern("g g")
                    .pattern("ccc")
                    .criterion("has_copper", conditionsFromTag(ConventionalItemTags.COPPER_INGOTS))
                    .offerTo(exporter)
                ModItems.TALL_CAGE_LAMPS.forEach {
                    val dye = DyeUtils.DYE_TAG_ORDER[ModItems.TALL_CAGE_LAMPS.indexOf(it)]
                    createShaped(RecipeCategory.REDSTONE, it, 3)
                        .input('c', Items.IRON_NUGGET)
                        .input('g', Items.GLOWSTONE_DUST)
                        .input('r', ConventionalItemTags.REDSTONE_DUSTS)
                        .input('d', dye)
                        .input('i', ConventionalItemTags.IRON_INGOTS)
                        .pattern("ccc")
                        .pattern("gdg")
                        .pattern("iri")
                        .criterion("has_iron", conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                        .offerTo(exporter)
                }
            }
        }
    }
}