package grauly.dustydecor

import grauly.dustydecor.screens.VacPipeReceiveStationScreen
import grauly.dustydecor.screens.VacPipeSendStationScreen
import grauly.dustydecor.screens.VacPipeStationScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens

object ModHandledScreens {

    fun init() {
        HandledScreens.register(ModScreenHandlerTypes.VAC_PIPE_STATION_SEND_SCREEN_HANDLER, ::VacPipeSendStationScreen)
        HandledScreens.register(ModScreenHandlerTypes.VAC_PIPE_STATION_RECEIVE_SCREEN_HANDLER, ::VacPipeReceiveStationScreen)
    }
}