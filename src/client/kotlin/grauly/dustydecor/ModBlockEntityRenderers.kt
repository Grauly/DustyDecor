package grauly.dustydecor

import grauly.dustydecor.blockentity.AlarmCageLampBlockEntityRenderer
import grauly.dustydecor.blockentity.VacPipeBlockEntityRenderer
import grauly.dustydecor.blockentity.VacPipeStationBlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers

object ModBlockEntityRenderers {

    fun init() {
        BlockEntityRenderers.register(ModBlockEntityTypes.ALARM_CAGE_LAMP_ENTITY, ::AlarmCageLampBlockEntityRenderer)
        BlockEntityRenderers.register(ModBlockEntityTypes.VAC_PIPE_ENTITY, ::VacPipeBlockEntityRenderer)
        BlockEntityRenderers.register(ModBlockEntityTypes.VAC_PIPE_STATION_ENTITY, ::VacPipeStationBlockEntityRenderer)
    }
}