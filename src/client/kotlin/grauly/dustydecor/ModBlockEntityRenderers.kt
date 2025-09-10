package grauly.dustydecor

import grauly.dustydecor.blockentity.TallCageLampBlockEntityRenderer
import grauly.dustydecor.blockentity.VacPipeBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

object ModBlockEntityRenderers {

    fun init() {
        BlockEntityRendererFactories.register(ModBlockEntityTypes.TALL_CAGE_LAMP_ENTITY, ::TallCageLampBlockEntityRenderer)
        BlockEntityRendererFactories.register(ModBlockEntityTypes.VAC_PIPE_ENTITY, ::VacPipeBlockEntityRenderer)
    }
}