package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.ItemDatagenWrapper
import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.component.ScrewdriverComponent
import grauly.dustydecor.component.WrenchComponent
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
        ItemDatagenWrapper.entries.forEach {
            builder.add(it.item, it.lang)
        }

        builder.add(SoundEventDatagen.getSubtitle(ModSoundEvents.BLOCK_VENT_LOCK), "Vent locks")
        builder.add(SoundEventDatagen.getSubtitle(ModSoundEvents.BLOCK_VENT_UNLOCK), "Vent unlocks")
        builder.add(SoundEventDatagen.getSubtitle(ModSoundEvents.BLOCK_VENT_RATTLE), "Vent rattles")
        builder.add(SoundEventDatagen.getSubtitle(ModSoundEvents.BLOCK_VAP_PIPE_REMOVE_WINDOW), "Window removed")
        builder.add(SoundEventDatagen.getSubtitle(ModSoundEvents.BLOCK_VAP_PIPE_ADD_WINDOW), "Window added")
        builder.add(SoundEventDatagen.getSubtitle(ModSoundEvents.ITEM_WRENCH_USE), "Wrench used")

        builder.add(WrenchComponent.TRANSLATION_KEY_0, "Can edit %s connections")
        builder.add(
            WrenchComponent.TRANSLATION_KEY_1,
            "Use on a exiting connection to attempt a different connection for it"
        )
        builder.add(ScrewdriverComponent.VAC_TUBE_TRANSLATION_KEY, "Can toggle windows on %s's")
        builder.add(ScrewdriverComponent.VENT_COVER_TRANSLATION_KEY, "Can lock/unlock %s's")
    }
}