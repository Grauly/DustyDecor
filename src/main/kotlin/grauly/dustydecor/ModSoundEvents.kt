package grauly.dustydecor

import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

object ModSoundEvents {

    /*
    Checklist for adding a new Sound:
    - Lang entry for subtitles
     */

    val BLOCK_VENT_LOCK = registerSound("block.vent.lock")
    val BLOCK_VENT_UNLOCK = registerSound("block.vent.unlock")
    val BLOCK_VENT_RATTLE = registerSound("block.vent.rattle")

    private fun registerSound(id: String): SoundEvent {
        val identifier: Identifier = Identifier.of(DustyDecorMod.MODID, id)
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier))
    }

    fun init() {
        //[Space intentionally left blank]
    }
}