package grauly.dustydecor

import grauly.dustydecor.screen.VacPipeReceiveStationScreenHandler
import grauly.dustydecor.screen.VacPipeSendStationScreenHandler
import net.minecraft.world.entity.player.Inventory
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.resources.ResourceLocation

object ModScreenHandlerTypes {

    val VAC_PIPE_STATION_SEND_SCREEN_HANDLER: MenuType<VacPipeSendStationScreenHandler> = register("vac_pipe_station_send", ::VacPipeSendStationScreenHandler)
    val VAC_PIPE_STATION_RECEIVE_SCREEN_HANDLER: MenuType<VacPipeReceiveStationScreenHandler> = register("vac_pipe_station_receive", ::VacPipeReceiveStationScreenHandler)

    private fun <T: AbstractContainerMenu> register(id: String, constructor: (Int, Inventory) -> T, ): MenuType<T> {
        return Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, id), MenuType<T>(constructor, FeatureFlagSet.of()))
    }

    fun init() {
        //[Space intentionally left blank]
    }
}