package grauly.dustydecor

import grauly.dustydecor.screens.VacPipeStationScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens

object ModHandledScreens {

    fun init() {
        HandledScreens.register(ModScreenHandlerTypes.VAC_PIPE_STATION_SCREEN_HANDLER, ::VacPipeStationScreen)
    }
}