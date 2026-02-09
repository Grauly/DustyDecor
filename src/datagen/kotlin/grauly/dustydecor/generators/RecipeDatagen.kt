package grauly.dustydecor.generators

import grauly.dustydecor.ModItems
import grauly.dustydecor.util.DyeUtils
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.world.item.Items
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class RecipeDatagen(
    output: FabricPackOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>,
) : FabricRecipeProvider(output, registriesFuture) {
    override fun getName(): String {
        return "Recipe Provider"
    }

    override fun createRecipeProvider(wrapper: HolderLookup.Provider, exporter: RecipeOutput): RecipeProvider {
        return object : RecipeProvider(wrapper, exporter) {
            override fun buildRecipes() {
                shapeless(RecipeCategory.DECORATIONS, ModItems.VENT_COVER, 2)
                    .requires(Items.IRON_TRAPDOOR)
                    .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                    .save(exporter)
                shapeless(RecipeCategory.DECORATIONS, Items.IRON_TRAPDOOR)
                    .requires(ModItems.VENT_COVER, 2)
                    .unlockedBy(getHasName(Items.IRON_TRAPDOOR), has(Items.IRON_TRAPDOOR))
                    .save(exporter)
                shaped(RecipeCategory.DECORATIONS, ModItems.VENT, 16)
                    .define('c', Items.IRON_TRAPDOOR)
                    .pattern("ccc")
                    .pattern("c c")
                    .pattern("ccc")
                    .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                    .save(exporter)
                shaped(RecipeCategory.TOOLS, ModItems.SCREWDRIVER)
                    .define('i', ConventionalItemTags.IRON_INGOTS)
                    .define('r', ConventionalItemTags.RED_DYES)
                    .pattern(" i")
                    .pattern("r ")
                    .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                    .save(exporter)
                shaped(RecipeCategory.TOOLS, ModItems.WRENCH)
                    .define('i', ConventionalItemTags.IRON_INGOTS)
                    .define('b', ConventionalItemTags.BLUE_DYES)
                    .define('t', Items.IRON_TRAPDOOR)
                    .pattern(" ti")
                    .pattern("ti ")
                    .pattern("b  ")
                    .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                    .save(exporter)
                shaped(RecipeCategory.REDSTONE, ModItems.VAC_PIPE, 6)
                    .define('c', ConventionalItemTags.COPPER_INGOTS)
                    .define('g', ConventionalItemTags.GLASS_PANES_COLORLESS)
                    .pattern("ccc")
                    .pattern("g g")
                    .pattern("ccc")
                    .unlockedBy("has_copper", has(ConventionalItemTags.COPPER_INGOTS))
                    .save(exporter)
                shapeless(RecipeCategory.REDSTONE, ModItems.VAC_PIPE_STATION)
                    .requires(ModItems.VAC_PIPE)
                    .requires(Items.HOPPER)
                    .unlockedBy("has_vac_pipe", has(ModItems.VAC_PIPE))
                    .save(exporter)
                shaped(RecipeCategory.MISC, ModItems.VAC_CAPSULE)
                    .define('n', ConventionalItemTags.IRON_NUGGETS)
                    .define('c', ConventionalItemTags.COPPER_INGOTS)
                    .pattern("nnn")
                    .pattern(" c ")
                    .pattern("nnn")
                    .unlockedBy("has_copper", has(ConventionalItemTags.COPPER_INGOTS))
                    .save(exporter)


                ModItems.TALL_CAGE_LAMPS.forEach {
                    val dye = DyeUtils.DYE_TAG_ORDER[ModItems.TALL_CAGE_LAMPS.indexOf(it)]
                    shaped(RecipeCategory.REDSTONE, it, 3)
                        .define('c', Items.IRON_NUGGET)
                        .define('g', Items.GLOWSTONE_DUST)
                        .define('r', ConventionalItemTags.REDSTONE_DUSTS)
                        .define('d', dye)
                        .define('i', ConventionalItemTags.IRON_INGOTS)
                        .pattern("ccc")
                        .pattern("gdg")
                        .pattern("iri")
                        .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                        .save(exporter)
                }
                ModItems.ALARM_CAGE_LAMPS.forEach {
                    val dye = DyeUtils.DYE_TAG_ORDER[ModItems.ALARM_CAGE_LAMPS.indexOf(it)]
                    shaped(RecipeCategory.REDSTONE, it, 3)
                        .define('c', Items.IRON_NUGGET)
                        .define('l', Items.REDSTONE_LAMP)
                        .define('d', dye)
                        .define('i', ConventionalItemTags.IRON_INGOTS)
                        .pattern("ccc")
                        .pattern(" l ")
                        .pattern("idi")
                        .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                        .save(exporter)
                }
                ModItems.WIDE_CAGE_LAMPS.forEach {
                    val dye = DyeUtils.DYE_TAG_ORDER[ModItems.WIDE_CAGE_LAMPS.indexOf(it)]
                    shaped(RecipeCategory.REDSTONE, it, 3)
                        .define('c', Items.IRON_NUGGET)
                        .define('g', Items.GLOWSTONE_DUST)
                        .define('d', dye)
                        .define('i', ConventionalItemTags.IRON_INGOTS)
                        .pattern("ccc")
                        .pattern("gdg")
                        .pattern("ici")
                        .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                        .save(exporter)
                }
                ModItems.STOOLS.forEach {
                    val dye = DyeUtils.DYE_TAG_ORDER[ModItems.STOOLS.indexOf(it)]
                    shaped(RecipeCategory.DECORATIONS, it, 1)
                        .define('c', Items.IRON_NUGGET)
                        .define('l', ConventionalItemTags.LEATHERS)
                        .define('d', dye)
                        .define('i', ConventionalItemTags.IRON_INGOTS)
                        .pattern("cdc")
                        .pattern("cli")
                        .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                        .save(exporter)
                }
                ModItems.CHAIRS.forEach {
                    val dye = DyeUtils.DYE_TAG_ORDER[ModItems.CHAIRS.indexOf(it)]
                    shaped(RecipeCategory.DECORATIONS, it, 1)
                        .define('n', Items.IRON_NUGGET)
                        .define('l', ConventionalItemTags.LEATHERS)
                        .define('d', dye)
                        .define('i', ConventionalItemTags.IRON_INGOTS)
                        .pattern("ld ")
                        .pattern("nln")
                        .pattern("nii")
                        .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                        .save(exporter)
                }
            }
        }
    }
}