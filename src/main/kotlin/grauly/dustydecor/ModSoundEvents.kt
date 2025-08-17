package grauly.dustydecor

import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

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
    val ITEM_WRENCH_USE = registerSound("item.wrench.use")

    private fun registerSound(id: String): SoundEvent {
        val identifier: Identifier = Identifier.of(DustyDecorMod.MODID, id)
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier))
    }

    fun init() {
        //[Space intentionally left blank]
    }
}