package grauly.dustydecor

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry
import net.minecraft.sounds.SoundEvent
import net.minecraft.resources.ResourceLocation

object ModSoundEvents {

    /*
    Checklist for adding a new Sound:
    - Sound event datagen
    - Lang entry for subtitles
     */

    val BLOCK_VENT_LOCK = registerSound("block.vent.lock")
    val BLOCK_VENT_UNLOCK = registerSound("block.vent.unlock")
    val BLOCK_VENT_RATTLE = registerSound("block.vent.rattle")
    val BLOCK_VAP_PIPE_ADD_WINDOW = registerSound("block.vac_pipe.add_window")
    val BLOCK_VAP_PIPE_REMOVE_WINDOW = registerSound("block.vac_pipe.remove_window")
    val BLOCK_LIGHTING_FIXTURE_INVERT = registerSound("block.lighting_fixture.invert")
    val BLOCK_LIGHTING_FIXTURE_REPAIR = registerSound("block.lighting_fixture.repair")
    val BLOCK_LIGHTING_FIXTURE_BREAK = registerSound("block.lighting_fixture.break")
    val BLOCK_LIGHTING_FIXTURE_TURN_ON = registerSound("block.lighting_fixture.turn_on")
    val BLOCK_LIGHTING_FIXTURE_TURN_OFF = registerSound("block.lighting_fixture.turn_off")
    val ITEM_WRENCH_USE = registerSound("item.wrench.use")
    val ITEM_SCREWDRIVER_USE = registerSound("item.screwdriver.use")

    private fun registerSound(id: String): SoundEvent {
        val identifier: ResourceLocation = ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, id)
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier))
    }

    fun init() {
        //[Space intentionally left blank]
    }
}