package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class BlockLootTableDatagen(
    dataOutput: FabricDataOutput?,
    registryLookup: CompletableFuture<HolderLookup.Provider>?
) : FabricBlockLootTableProvider(dataOutput, registryLookup) {

    override fun generate() {
        BlockDatagenWrapper.entries.filter { it.generateLootTable }.forEach { dropSelf(it.block) }
    }
}