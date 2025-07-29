package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.ModSoundEvents
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class LangDatagen(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>?
) : FabricLanguageProvider(output, "en_us", registriesFuture) {
    override fun generateTranslations(wrapper: RegistryWrapper.WrapperLookup, builder: TranslationBuilder) {
        BlockDatagenWrapper.entries.forEach {
            builder.add(it.block, it.name)
            builder.add(it.block.asItem(), it.name)
        }
        builder.add(SoundEventDatagen.getSubtitle(ModSoundEvents.BLOCK_VENT_LOCK), "Vent locks")
        builder.add(SoundEventDatagen.getSubtitle(ModSoundEvents.BLOCK_VENT_UNLOCK), "Vent unlocks")
        builder.add(SoundEventDatagen.getSubtitle(ModSoundEvents.BLOCK_VENT_RATTLE), "Vent rattles")
    }
}