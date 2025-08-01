package grauly.dustydecor.generators

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModSoundEvents
import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider
import net.minecraft.data.DataOutput
import net.minecraft.registry.RegistryWrapper
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import java.util.concurrent.CompletableFuture

class SoundEventDatagen(
    output: DataOutput?,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>?
) : FabricSoundsProvider(output, registriesFuture) {
    override fun getName(): String = "Sound Event generator"

    override fun configure(wrapper: RegistryWrapper.WrapperLookup, exporter: SoundExporter) {
        simpleRedirect(ModSoundEvents.BLOCK_VENT_LOCK, SoundEvents.BLOCK_VAULT_DEACTIVATE, SoundCategory.BLOCKS, exporter)
        simpleRedirect(ModSoundEvents.BLOCK_VENT_UNLOCK, SoundEvents.BLOCK_VAULT_ACTIVATE, SoundCategory.BLOCKS, exporter)
        simpleRedirect(ModSoundEvents.BLOCK_VENT_RATTLE, SoundEvents.BLOCK_VAULT_CLOSE_SHUTTER, SoundCategory.BLOCKS, exporter)
    }

    private fun simpleRedirect(from: SoundEvent, to: SoundEvent, soundCategory: SoundCategory, exporter: SoundExporter) {
        exporter.add(
            from,
            SoundTypeBuilder.of()
                .sound(SoundTypeBuilder.EntryBuilder.ofEvent(to))
                .category(soundCategory)
                .subtitle(getSubtitle(from))
        )
    }

    companion object {
        fun getSubtitle(soundEvent: SoundEvent): String = "subtitles.${DustyDecorMod.MODID}.${soundEvent.id.path}"
    }
}