package grauly.dustydecor

import grauly.dustydecor.screens.VacPipeReceiveStationScreen
import grauly.dustydecor.screens.VacPipeSendStationScreen
import grauly.dustydecor.screens.VacPipeStationScreen
import net.minecraft.client.gui.screens.MenuScreens

object ModHandledScreens {

    fun init() {
        MenuScreens.register(ModScreenHandlerTypes.VAC_PIPE_STATION_SEND_SCREEN_HANDLER, ::VacPipeSendStationScreen)
        MenuScreens.register(ModScreenHandlerTypes.VAC_PIPE_STATION_RECEIVE_SCREEN_HANDLER, ::VacPipeReceiveStationScreen)
    }
}