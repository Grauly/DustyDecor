package grauly.dustydecor

import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.minecraft.client.render.BlockRenderLayer

object ModBlockLayerRenderMapEntries {
    fun init() {
        BlockRenderLayerMap.putBlock(ModBlocks.VENT_COVER, BlockRenderLayer.CUTOUT)
    }
}