package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.ItemDatagenWrapper
import grauly.dustydecor.ModConventionalItemTags
import grauly.dustydecor.ModItemTags
import grauly.dustydecor.ModItems
import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.component.BulkGoopSizeComponent
import grauly.dustydecor.component.ScrewdriverComponent
import grauly.dustydecor.component.WrenchComponent
import grauly.dustydecor.item.BulkVoidGoopItem
import grauly.dustydecor.item.OutsideCrystalShardItem
import kotlinx.coroutines.flow.combine
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.item.Item
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.TagKey
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
            if (it.lang == null) return@forEach
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

        builder.add(WrenchComponent.VAC_PIPE_TRANSLATION_KEY, "Can edit %s connections")
        builder.add(
            WrenchComponent.VAC_PIPE_CONNECTION_TRANSLATION_KEY,
            "Use on a exiting connection to attempt a different connection for it"
        )
        builder.add(WrenchComponent.VAC_PIPE_STATION_TRANSLATION_KEY, "Can toggle whether %s's send or receive")
        builder.add(WrenchComponent.LAMPS_TRANSLATION_KEY, "Can break and repair some lamps")
        builder.add(ScrewdriverComponent.VAC_TUBE_TRANSLATION_KEY, "Can toggle windows on %s's")
        builder.add(ScrewdriverComponent.VENT_COVER_TRANSLATION_KEY, "Can lock/unlock %s's")
        builder.add(ScrewdriverComponent.LAMPS_TRANSLATION_KEY, "Can invert some lamps")

        builder.add(tagTranslationKey(ModConventionalItemTags.SCREWDRIVER_TOOLS), "Screwdrivers")
        builder.add(tagTranslationKey(ModItemTags.VOID_GOOP), "Void Goop")

        builder.add(OutsideCrystalShardItem.VOID_GOOP_REMOVAL, "Removed %s %s")
        builder.add(OutsideCrystalShardItem.VOID_GOOP_FIND, "Found %s %s")

        builder.add(BulkGoopSizeComponent.SIZE_INDICATOR, "Places %s %s at once")
        builder.add(BulkGoopSizeComponent.EXPLANATION, "Scroll while hovering to change amount")
        builder.add(BulkGoopSizeComponent.EXPLANATION_L2, "Press shift to increase by 10")
        builder.add(BulkVoidGoopItem.VOID_GOOP_PLACED, "Placed %s %s")

        builder.add("death.attack.void_consumption", "%s won a staring contest with the void")
        builder.add("death.attack.void_consumption.player", "%s lost themselves to the void while fleeing from %s")
        builder.add("death.attack.void_consumption.item", "%s was shunted into the void by %s using %s")

        subtitles.forEach { (e, t) ->
            builder.add(SoundEventDatagen.getSubtitle(e), t)
        }
    }

    private fun sub(event: SoundEvent, translation: String) {
        subtitles[event] = translation
    }

    private fun tagTranslationKey(itemTagKey: TagKey<Item>): String {
        return "tag.item.${itemTagKey.id.toTranslationKey().replace("/",".")}"
    }
}