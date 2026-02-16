package grauly.dustydecor.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModSoundEvents
import net.minecraft.sounds.SoundEvent

class ToolUseSoundComponent(val sound: SoundEvent) {
    companion object {
        val CODEC: Codec<ToolUseSoundComponent> = RecordCodecBuilder.create<ToolUseSoundComponent> {
            it.group(
                SoundEvent.DIRECT_CODEC.fieldOf("sound").forGetter { component -> component.sound }
            ).apply(it, ::ToolUseSoundComponent)
        }
        val SCREWDRIVER = ToolUseSoundComponent(ModSoundEvents.ITEM_SCREWDRIVER_USE)
        val WRENCH = ToolUseSoundComponent(ModSoundEvents.ITEM_WRENCH_USE)
    }
}