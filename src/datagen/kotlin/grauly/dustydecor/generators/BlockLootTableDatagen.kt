package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.ImpactBreakable
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider
import net.minecraft.advancements.criterion.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import java.util.concurrent.CompletableFuture

class BlockLootTableDatagen(
    dataOutput: FabricPackOutput,
    registryLookup: CompletableFuture<HolderLookup.Provider>
) : FabricBlockLootSubProvider(dataOutput, registryLookup) {

    override fun generate() {
        BlockDatagenWrapper.entries.filter { it.generateLootTable }.forEach { dropSelf(it.block) }
    }
}