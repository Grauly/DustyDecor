package grauly.dustydecor

import grauly.dustydecor.blockentity.AlarmCageLampRenderer
import grauly.dustydecor.blockentity.PhoneRenderer
import grauly.dustydecor.blockentity.VacPipeRenderer
import grauly.dustydecor.blockentity.VacPipeStationRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers

object ModBlockEntityRenderers {

    fun init() {
        BlockEntityRenderers.register(ModBlockEntityTypes.ALARM_CAGE_LAMP_ENTITY, ::AlarmCageLampRenderer)
        BlockEntityRenderers.register(ModBlockEntityTypes.VAC_PIPE_ENTITY, ::VacPipeRenderer)
        BlockEntityRenderers.register(ModBlockEntityTypes.VAC_PIPE_STATION_ENTITY, ::VacPipeStationRenderer)
        BlockEntityRenderers.register(ModBlockEntityTypes.PHONE_ENTITY, ::PhoneRenderer)
    }
}