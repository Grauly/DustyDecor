package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.ModBlockTags
import grauly.dustydecor.ModBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import java.util.concurrent.CompletableFuture

class BlockTagDatagen(
    output: FabricDataOutput?,
    registriesFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricTagProvider.BlockTagProvider(output, registriesFuture) {

    override fun addTags(wrapper: HolderLookup.Provider) {
        BlockDatagenWrapper.entries.filter { it.toolNeed.needsProcessing() }.forEach {
            valueLookupBuilder(BlockDatagenWrapper.getToolNeed(it.toolNeed.toolNeed)).add(it.block)
            val override = it.toolNeed.override ?: return@forEach
            valueLookupBuilder(override).add(it.block)
        }
        BlockDatagenWrapper.entries.filter { it.mineable.needsProcessing() }.forEach {
            val mineableBy = it.mineable.tools
            mineableBy.forEach { mb ->
                val mineable = BlockDatagenWrapper.getMineable(mb)
                if (mineable != null) {
                    valueLookupBuilder(mineable).add(it.block)
                }
            }
        }
        valueLookupBuilder(ModBlockTags.LARGE_VENT_CONNECTABLE).add(
            ModBlocks.VENT,
            ModBlocks.VENT_COVER
        )
        valueLookupBuilder(BlockTags.CLIMBABLE).add(
            ModBlocks.VENT
        )
    }
}