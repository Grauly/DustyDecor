package grauly.dustydecor.generators

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModSoundEvents
import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider
import net.minecraft.data.PackOutput
import net.minecraft.core.HolderLookup
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

class SoundEventDatagen(
    output: PackOutput?,
    registriesFuture: CompletableFuture<HolderLookup.Provider>?
) : FabricSoundsProvider(output, registriesFuture) {
    override fun getName(): String = "Sound Event generator"

    override fun configure(wrapper: HolderLookup.Provider, exporter: SoundExporter) {
        simpleRedirect(
            ModSoundEvents.BLOCK_VENT_LOCK,
            SoundEvents.VAULT_DEACTIVATE,
            SoundSource.BLOCKS,
            exporter
        )
        simpleRedirect(
            ModSoundEvents.BLOCK_VENT_UNLOCK,
            SoundEvents.VAULT_ACTIVATE,
            SoundSource.BLOCKS,
            exporter
        )
        simpleRedirect(
            ModSoundEvents.BLOCK_VENT_RATTLE,
            SoundEvents.VAULT_CLOSE_SHUTTER,
            SoundSource.BLOCKS,
            exporter
        )
        multiRedirect(
            ModSoundEvents.BLOCK_VAP_PIPE_ADD_WINDOW,
            SoundSource.BLOCKS,
            exporter,
            SoundEvents.ITEM_FRAME_ADD_ITEM
        )
        multiRedirect(
            ModSoundEvents.BLOCK_VAP_PIPE_REMOVE_WINDOW,
            SoundSource.BLOCKS,
            exporter,
            SoundEvents.ITEM_FRAME_REMOVE_ITEM
        )
        fromFiles(
            ModSoundEvents.ITEM_WRENCH_USE,
            SoundSource.PLAYERS,
            exporter,
            "wrench1",
            "wrench2",
            "wrench3",
            "wrench4",
            "wrench5"
        )
        fromFiles(
            ModSoundEvents.ITEM_SCREWDRIVER_USE,
            SoundSource.PLAYERS,
            exporter,
            "screwdriver1",
            "screwdriver2",
            "screwdriver3",
            "screwdriver4",
            "screwdriver5",
            "screwdriver6",
        )
        simpleRedirect(
            ModSoundEvents.BLOCK_LIGHTING_FIXTURE_TURN_ON,
            SoundEvents.COPPER_BULB_TURN_ON,
            SoundSource.BLOCKS,
            exporter
        )
        simpleRedirect(
            ModSoundEvents.BLOCK_LIGHTING_FIXTURE_TURN_OFF,
            SoundEvents.COPPER_BULB_TURN_OFF,
            SoundSource.BLOCKS,
            exporter
        )
        simpleRedirect(
            ModSoundEvents.BLOCK_LIGHTING_FIXTURE_BREAK,
            SoundEvents.GLASS_BREAK,
            SoundSource.BLOCKS,
            exporter
        )
        simpleRedirect(
            ModSoundEvents.BLOCK_LIGHTING_FIXTURE_REPAIR,
            SoundEvents.IRON_GOLEM_REPAIR,
            SoundSource.BLOCKS,
            exporter
        )
        multiRedirect(
            ModSoundEvents.BLOCK_LIGHTING_FIXTURE_INVERT,
            SoundSource.BLOCKS,
            exporter,
            SoundEvents.STONE_BUTTON_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF
        )
    }

    private fun simpleRedirect(
        from: SoundEvent,
        to: SoundEvent,
        soundCategory: SoundSource,
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
        soundCategory: SoundSource,
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
        soundCategory: SoundSource,
        exporter: SoundExporter,
        vararg files: ResourceLocation
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
        soundCategory: SoundSource,
        exporter: SoundExporter,
        vararg files: String
    ) {
        fromFiles(event, soundCategory, exporter, *files.asList().map { ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, it) }.toTypedArray())
    }

    companion object {
        fun getSubtitle(soundEvent: SoundEvent): String = "subtitles.${DustyDecorMod.MODID}.${soundEvent.location.path}"
    }
}