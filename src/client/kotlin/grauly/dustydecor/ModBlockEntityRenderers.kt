package grauly.dustydecor

import grauly.dustydecor.blockentity.AlarmCageLampBlockEntityRenderer
import grauly.dustydecor.blockentity.VacPipeBlockEntityRenderer
import grauly.dustydecor.blockentity.VacPipeStationBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

object ModBlockEntityRenderers {

    fun init() {
        BlockEntityRendererFactories.register(ModBlockEntityTypes.ALARM_CAGE_LAMP_ENTITY, ::AlarmCageLampBlockEntityRenderer)
        BlockEntityRendererFactories.register(ModBlockEntityTypes.VAC_PIPE_ENTITY, ::VacPipeBlockEntityRenderer)
        BlockEntityRendererFactories.register(ModBlockEntityTypes.VAC_PIPE_STATION_ENTITY, ::VacPipeStationBlockEntityRenderer)
    }
}