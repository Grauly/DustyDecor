package grauly.dustydecor

import grauly.dustydecor.event.VoidGoopLookHandler
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents

object ModEvents {
    fun init() {
        ServerTickEvents.END_LEVEL_TICK.register {
            VoidGoopLookHandler.onEndTick(it)
        }
    }
}