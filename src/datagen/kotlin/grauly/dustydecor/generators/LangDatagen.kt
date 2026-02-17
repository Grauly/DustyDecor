package grauly.dustydecor.generators

import grauly.dustydecor.BlockDatagenWrapper
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ItemDatagenWrapper
import grauly.dustydecor.ModConventionalItemTags
import grauly.dustydecor.ModItemTags
import grauly.dustydecor.ModSoundEvents
import grauly.dustydecor.block.furniture.SittableFurnitureBlock
import grauly.dustydecor.component.BulkGoopSizeComponent
import grauly.dustydecor.component.ToolComponents
import grauly.dustydecor.entity.SeatEntity
import grauly.dustydecor.entity.SitResultType
import grauly.dustydecor.item.BulkVoidGoopItem
import grauly.dustydecor.item.OutsideCrystalShardItem
import grauly.dustydecor.screens.VacPipeStationScreen
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.world.item.Item
import net.minecraft.core.HolderLookup
import net.minecraft.tags.TagKey
import net.minecraft.sounds.SoundEvent
import java.util.concurrent.CompletableFuture

class LangDatagen(
    output: FabricPackOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricLanguageProvider(output, "en_us", registriesFuture) {
    private val subtitles: MutableMap<SoundEvent, String> = mutableMapOf()
    override fun generateTranslations(wrapper: HolderLookup.Provider, builder: TranslationBuilder) {
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

        builder.add(
            ToolComponents.VAC_TUBE_EDIT.lines[0],
            "Can edit %s connections:"
        )
        builder.add(
            ToolComponents.VAC_TUBE_EDIT.lines[1],
            "Use on a exiting connection to attempt a different connection for it"
        )
        builder.add(
            ToolComponents.VAC_TUBE_EDIT.lines[2],
            "Use on the core of the pipe to attempt to fix a broken connection"
        )
        builder.add(ToolComponents.VAC_STATION_INVERT.translationKey, "Can toggle whether %s's send or receive")
        builder.add(ToolComponents.LAMPS_REPAIR.translationKey, "Can break and repair some lamps")
        builder.add(ToolComponents.VAC_TUBE_WINDOW_TOGGLE.translationKey, "Can toggle windows on %s's")
        builder.add(ToolComponents.VENT_LOCK_TOGGLE.translationKey, "Can lock/unlock %s's")
        builder.add(ToolComponents.LAMPS_INVERT.translationKey, "Can invert some lamps")
        builder.add(ToolComponents.SMALL_GLASS_TABLE_STRIP_PANE.translationKey, "Can safely remove the glass pane from small glass tables")

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

        builder.add(VacPipeStationScreen.COPPER_GOLEM_MODE_TRANSLATION_KEY, "Copper Golem Behavior: ")
        builder.add(VacPipeStationScreen.COPPER_GOLEM_MODE_TRANSLATION_KEY_INTERACT, "Interact")
        builder.add(VacPipeStationScreen.COPPER_GOLEM_MODE_TRANSLATION_KEY_INTERACT_NARRATION, "Interact, Copper Golems will supply capsules, or take received ones")
        builder.add(VacPipeStationScreen.COPPER_GOLEM_MODE_TRANSLATION_KEY_IGNORE, "Ignore")
        builder.add(VacPipeStationScreen.COPPER_GOLEM_MODE_TRANSLATION_KEY_IGNORE_NARRATION, "Ignore, Copper Golems will not interact with this station")
        builder.add(VacPipeStationScreen.REDSTONE_MODE_TRANSLATION_KEY, "Redstone Emission: ")
        builder.add(VacPipeStationScreen.REDSTONE_MODE_TRANSLATION_KEY_ON_RECEIVE, "On Receive")
        builder.add(VacPipeStationScreen.REDSTONE_MODE_TRANSLATION_KEY_ON_RECEIVE_NARRATION, "On Receive, emits a redstone signal when a capsule arrives")
        builder.add(VacPipeStationScreen.REDSTONE_MODE_TRANSLATION_KEY_ON_SEND, "On Send")
        builder.add(VacPipeStationScreen.REDSTONE_MODE_TRANSLATION_KEY_ON_SEND_NARRATION, "On Send, emits a redstone signal when a capsule gets sent")
        builder.add(VacPipeStationScreen.REDSTONE_MODE_TRANSLATION_KEY_WHILE_EMPTY, "While Empty")
        builder.add(VacPipeStationScreen.REDSTONE_MODE_TRANSLATION_KEY_WHILE_EMPTY_NARRATION, "While Empty, emits a redstone signal when no capsule is present")
        builder.add(VacPipeStationScreen.REDSTONE_MODE_TRANSLATION_KEY_NONE, "Never")
        builder.add(VacPipeStationScreen.REDSTONE_MODE_TRANSLATION_KEY_NONE_NARRATION, "Never, does not emit a redstone signal")
        builder.add(VacPipeStationScreen.SENDING_MODE_TRANSLATION_KEY, "Sending Mode: ")
        builder.add(VacPipeStationScreen.SENDING_MODE_TRANSLATION_KEY_MANUAL, "Manual")
        builder.add(VacPipeStationScreen.SENDING_MODE_TRANSLATION_KEY_MANUAL_NARRATION, "Manual, press the GUI button to send a capsule")
        builder.add(VacPipeStationScreen.SENDING_MODE_TRANSLATION_KEY_REDSTONE, "Redstone")
        builder.add(VacPipeStationScreen.SENDING_MODE_TRANSLATION_KEY_REDSTONE_NARRATION, "Redstone, send capsules whenever a redstone signal is present")
        builder.add(VacPipeStationScreen.SENDING_MODE_TRANSLATION_KEY_AUTOMATIC, "Automatic")
        builder.add(VacPipeStationScreen.SENDING_MODE_TRANSLATION_KEY_AUTOMATIC_NARRATION, "Automatic, send a capsule when one is present")

        builder.add("entity.${DustyDecorMod.MODID}.seat", "Seat")

        builder.add(SitResultType.SUCCESS.messageTranslationKey, "Now sitting down")
        builder.add(SitResultType.OCCUPIED.messageTranslationKey, "Already occupied")
        builder.add(SitResultType.ALREADY_SITTING.messageTranslationKey, "Already sitting")
        builder.add(SitResultType.TOO_FAR.messageTranslationKey, "Too far away")
        builder.add(SitResultType.NONE.messageTranslationKey, "The seat is a lie")

        subtitles.forEach { (e, t) ->
            builder.add(SoundEventDatagen.getSubtitle(e), t)
        }
    }

    private fun sub(event: SoundEvent, translation: String) {
        subtitles[event] = translation
    }

    private fun tagTranslationKey(itemTagKey: TagKey<Item>): String {
        return "tag.item.${itemTagKey.location.toLanguageKey().replace("/",".")}"
    }
}