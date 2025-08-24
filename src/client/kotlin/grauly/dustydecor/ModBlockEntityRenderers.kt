package grauly.dustydecor

import grauly.dustydecor.blockentity.TallCageLampBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

object ModBlockEntityRenderers {

    fun init() {
        BlockEntityRendererFactories.register(ModBlockEntityTypes.TALL_CAGE_LAMP_ENTITY, ::TallCageLampBlockEntityRenderer)
    }
}