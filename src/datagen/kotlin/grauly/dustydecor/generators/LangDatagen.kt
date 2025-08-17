package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.ItemDatagenWrapper
import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.component.ScrewdriverComponent
import grauly.dustydecor.component.WrenchComponent
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import net.minecraft.sound.SoundEvent
import java.util.concurrent.CompletableFuture

class LangDatagen(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>?
) : FabricLanguageProvider(output, "en_us", registriesFuture) {
    private val subtitles: MutableMap<SoundEvent, String> = mutableMapOf()
    override fun generateTranslations(wrapper: RegistryWrapper.WrapperLookup, builder: TranslationBuilder) {
        BlockDatagenWrapper.entries.forEach {
            builder.add(it.block, it.name)
            builder.add(it.block.asItem(), it.name)
        }
        ItemDatagenWrapper.entries.forEach {
            builder.add(it.item, it.lang)
        }


        sub(ModSoundEvents.BLOCK_VENT_LOCK, "Vent locks")
        sub(ModSoundEvents.BLOCK_VENT_UNLOCK, "Vent unlocks")
        sub(ModSoundEvents.BLOCK_VENT_RATTLE, "Vent rattles")

        sub(ModSoundEvents.BLOCK_VAP_PIPE_REMOVE_WINDOW, "Window removed")
        sub(ModSoundEvents.BLOCK_VAP_PIPE_ADD_WINDOW, "Window added")

        sub(ModSoundEvents.ITEM_WRENCH_USE, "Wrench wrenches")
        sub(ModSoundEvents.ITEM_SCREWDRIVER_USE, "Screwdriver ratchets")

        sub(ModSoundEvents.BLOCK_LIGHTING_FIXTURE_INVERT, "Lighting fixture inverted")
        sub(ModSoundEvents.BLOCK_LIGHTING_FIXTURE_REPAIR, "Lighting fixture repaired")
        sub(ModSoundEvents.BLOCK_LIGHTING_FIXTURE_BREAK, "Lighting fixture breaks")
        sub(ModSoundEvents.BLOCK_LIGHTING_FIXTURE_TURN_ON, "Lighting fixture turns on")
        sub(ModSoundEvents.BLOCK_LIGHTING_FIXTURE_TURN_OFF, "Lighting fixture turns off")

        builder.add(WrenchComponent.TRANSLATION_KEY_0, "Can edit %s connections")
        builder.add(
            WrenchComponent.TRANSLATION_KEY_1,
            "Use on a exiting connection to attempt a different connection for it"
        )
        builder.add(ScrewdriverComponent.VAC_TUBE_TRANSLATION_KEY, "Can toggle windows on %s's")
        builder.add(ScrewdriverComponent.VENT_COVER_TRANSLATION_KEY, "Can lock/unlock %s's")

        subtitles.forEach { (e, t) ->
            builder.add(SoundEventDatagen.getSubtitle(e), t)
        }
    }

    private fun sub(event: SoundEvent, translation: String) {
        subtitles[event] = translation
    }
}