package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class BlockLootTableDatagen(
    dataOutput: FabricPackOutput?,
    registryLookup: CompletableFuture<HolderLookup.Provider>?
) : FabricBlockLootSubProvider(dataOutput, registryLookup) {

    override fun generate() {
        BlockDatagenWrapper.entries.filter { it.generateLootTable }.forEach { dropSelf(it.block) }
    }
}