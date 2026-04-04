package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.layered.LayerThresholdSpreadingBlock
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider
import net.minecraft.advancements.criterion.StatePropertiesPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import java.util.concurrent.CompletableFuture

class BlockLootTableDatagen(
    dataOutput: FabricPackOutput,
    registryLookup: CompletableFuture<HolderLookup.Provider>
) : FabricBlockLootSubProvider(dataOutput, registryLookup) {

    override fun generate() {
        BlockDatagenWrapper.entries.filter { it.generateLootTable }.forEach { dropSelf(it.block) }
        add(
            ModBlocks.CRIMSON_SAND,
            { block ->
                LootTable.lootTable().withPool(
                    LootPool.lootPool().add(
                        applyExplosionCondition(
                            block,
                            AlternativesEntry.alternatives(
                                LayerThresholdSpreadingBlock.LAYERS.possibleValues,
                                { layers ->
                                    LootItem.lootTableItem(ModItems.CRIMSON_SAND)
                                        .`when`(
                                            LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(
                                                    StatePropertiesPredicate.Builder.properties().hasProperty(
                                                        LayerThresholdSpreadingBlock.LAYERS, layers
                                                    )
                                                )
                                        )
                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(layers.toFloat())))
                                }
                            )
                        )
                    )
                )
            }
        )
    }
}