package grauly.dustydecor.generators

import grauly.dustydecor.ItemDatagenWrapper
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider
import net.minecraft.core.HolderLookup
import net.minecraft.tags.ItemTags
import java.util.concurrent.CompletableFuture

class ItemTagDatagen(
    output: FabricPackOutput?,
    registriesFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricTagsProvider.ItemTagsProvider(output, registriesFuture) {

    override fun addTags(wrapper: HolderLookup.Provider) {
        val swords = valueLookupBuilder(ItemTags.SWORDS)
        val pickaxes = valueLookupBuilder(ItemTags.PICKAXES)
        val axes = valueLookupBuilder(ItemTags.AXES)
        val shovels = valueLookupBuilder(ItemTags.SHOVELS)
        val hoes = valueLookupBuilder(ItemTags.HOES)
        ItemDatagenWrapper.entries.filter { it.tags.isNotEmpty() }.forEach {
            it.tags.forEach { tag -> valueLookupBuilder(tag).add(it.item) }
        }
        ItemDatagenWrapper.entries.filter { it.toolSpec.needsProcessing() }.forEach {
            val toolSpec = it.toolSpec
            if (toolSpec.sword) swords.add(it.item)
            if (toolSpec.pickaxe) pickaxes.add(it.item)
            if (toolSpec.axe) axes.add(it.item)
            if (toolSpec.shovel) shovels.add(it.item)
            if (toolSpec.hoe) hoes.add(it.item)
        }
    }
}