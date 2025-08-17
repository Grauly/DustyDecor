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
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class SoundEventDatagen(
    output: DataOutput?,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>?
) : FabricSoundsProvider(output, registriesFuture) {
    override fun getName(): String = "Sound Event generator"

    override fun configure(wrapper: RegistryWrapper.WrapperLookup, exporter: SoundExporter) {
        simpleRedirect(
            ModSoundEvents.BLOCK_VENT_LOCK,
            SoundEvents.BLOCK_VAULT_DEACTIVATE,
            SoundCategory.BLOCKS,
            exporter
        )
        simpleRedirect(
            ModSoundEvents.BLOCK_VENT_UNLOCK,
            SoundEvents.BLOCK_VAULT_ACTIVATE,
            SoundCategory.BLOCKS,
            exporter
        )
        simpleRedirect(
            ModSoundEvents.BLOCK_VENT_RATTLE,
            SoundEvents.BLOCK_VAULT_CLOSE_SHUTTER,
            SoundCategory.BLOCKS,
            exporter
        )
        multiRedirect(
            ModSoundEvents.BLOCK_VAP_PIPE_ADD_WINDOW,
            SoundCategory.BLOCKS,
            exporter,
            SoundEvents.BLOCK_VAULT_ACTIVATE,
            SoundEvents.BLOCK_GLASS_PLACE
        )
        multiRedirect(
            ModSoundEvents.BLOCK_VAP_PIPE_REMOVE_WINDOW,
            SoundCategory.BLOCKS,
            exporter,
            SoundEvents.BLOCK_VAULT_DEACTIVATE
        )
        fromFiles(
            ModSoundEvents.ITEM_WRENCH_USE,
            SoundCategory.PLAYERS,
            exporter,
            "wrench1",
            "wrench2",
            "wrench3",
            "wrench4",
            "wrench5"
        )
    }

    private fun simpleRedirect(
        from: SoundEvent,
        to: SoundEvent,
        soundCategory: SoundCategory,
        exporter: SoundExporter
    ) {
        exporter.add(
            from,
            SoundTypeBuilder.of()
                .sound(SoundTypeBuilder.EntryBuilder.ofEvent(to))
                .category(soundCategory)
                .subtitle(getSubtitle(from))
        )
    }

    private fun multiRedirect(
        from: SoundEvent,
        soundCategory: SoundCategory,
        exporter: SoundExporter,
        vararg to: SoundEvent
    ) {
        val soundBuilder = SoundTypeBuilder.of()
        to.iterator().forEach { soundBuilder.sound(SoundTypeBuilder.EntryBuilder.ofEvent(it)) }
        exporter.add(
            from,
            soundBuilder
                .category(soundCategory)
                .subtitle(getSubtitle(from))
        )
    }

    private fun fromFiles(
        event: SoundEvent,
        soundCategory: SoundCategory,
        exporter: SoundExporter,
        vararg files: Identifier
    ) {
        val soundBuilder = SoundTypeBuilder.of()
        files.iterator().forEach { soundBuilder.sound(SoundTypeBuilder.EntryBuilder.ofFile(it)) }
        exporter.add(
            event,
            soundBuilder
                .category(soundCategory)
                .subtitle(getSubtitle(event))
        )
    }

    private fun fromFiles(
        event: SoundEvent,
        soundCategory: SoundCategory,
        exporter: SoundExporter,
        vararg files: String
    ) {
        fromFiles(event, soundCategory, exporter, *files.asList().map { Identifier.of(DustyDecorMod.MODID, it) }.toTypedArray())
    }

    companion object {
        fun getSubtitle(soundEvent: SoundEvent): String = "subtitles.${DustyDecorMod.MODID}.${soundEvent.id.path}"
    }
}