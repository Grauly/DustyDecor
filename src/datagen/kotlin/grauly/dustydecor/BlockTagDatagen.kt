package grauly.dustydecor

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.BlockTags
import java.util.concurrent.CompletableFuture

class BlockTagDatagen(
    output: FabricDataOutput?,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>?
) : FabricTagProvider.BlockTagProvider(output, registriesFuture) {

    override fun configure(wrapper: RegistryWrapper.WrapperLookup) {
        valueLookupBuilder(ModBlockTags.LARGE_VENT_CONNECTABLE).add(
            ModBlocks.VENT,
            ModBlocks.VENT_COVER
        )
        valueLookupBuilder(BlockTags.CLIMBABLE).add(
            ModBlocks.VENT
        )
        valueLookupBuilder(BlockTags.NEEDS_STONE_TOOL).add(
            ModBlocks.VENT,
            ModBlocks.VENT_COVER
        )
        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE).add(
            ModBlocks.VENT,
            ModBlocks.VENT_COVER
        )
    }
}