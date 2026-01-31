package grauly.dustydecor

import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.chunk.ChunkSectionLayer

object ModBlockLayerRenderMapEntries {
    fun init() {
        BlockRenderLayerMap.putBlock(ModBlocks.VENT_COVER, ChunkSectionLayer.CUTOUT)
        BlockRenderLayerMap.putBlock(ModBlocks.VAC_PIPE, ChunkSectionLayer.CUTOUT)
        ModBlocks.TALL_CAGE_LAMPS.forEach {
            BlockRenderLayerMap.putBlock(it, ChunkSectionLayer.CUTOUT)
        }
        ModBlocks.WIDE_CAGE_LAMPS.forEach {
            BlockRenderLayerMap.putBlock(it, ChunkSectionLayer.CUTOUT)
        }
        ModBlocks.ALARM_CAGE_LAMPS.forEach {
            BlockRenderLayerMap.putBlock(it, ChunkSectionLayer.CUTOUT)
        }
        ModBlocks.TUBE_LAMPS.forEach {
            BlockRenderLayerMap.putBlock(it, ChunkSectionLayer.CUTOUT)
        }
    }
}