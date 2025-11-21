package grauly.dustydecor

import grauly.dustydecor.screen.VacPipeReceiveStationScreenHandler
import grauly.dustydecor.screen.VacPipeSendStationScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object ModScreenHandlerTypes {

    val VAC_PIPE_STATION_SEND_SCREEN_HANDLER: ScreenHandlerType<VacPipeSendStationScreenHandler> = register("vac_pipe_station_send", ::VacPipeSendStationScreenHandler)
    val VAC_PIPE_STATION_RECEIVE_SCREEN_HANDLER: ScreenHandlerType<VacPipeReceiveStationScreenHandler> = register("vac_pipe_station_receive", ::VacPipeReceiveStationScreenHandler)

    private fun <T: ScreenHandler> register(id: String, constructor: (Int, PlayerInventory) -> T, ): ScreenHandlerType<T> {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(DustyDecorMod.MODID, id), ScreenHandlerType<T>(constructor, FeatureSet.empty()))
    }

    fun init() {
        //[Space intentionally left blank]
    }
}