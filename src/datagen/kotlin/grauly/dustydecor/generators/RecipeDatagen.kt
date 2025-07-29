package grauly.dustydecor.generators

import grauly.dustydecor.ModItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
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
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
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
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .offerTo(exporter)
            }
        }
    }
}