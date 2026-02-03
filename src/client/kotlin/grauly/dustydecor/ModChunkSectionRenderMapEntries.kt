package grauly.dustydecor

import net.fabricmc.fabric.api.client.rendering.v1.ChunkSectionLayerMap
import net.minecraft.client.renderer.chunk.ChunkSectionLayer

object ModChunkSectionRenderMapEntries {
    fun init() {
        ChunkSectionLayerMap.putBlock(ModBlocks.VENT_COVER, ChunkSectionLayer.CUTOUT)
        ChunkSectionLayerMap.putBlock(ModBlocks.VAC_PIPE, ChunkSectionLayer.CUTOUT)
        ModBlocks.TALL_CAGE_LAMPS.forEach {
            ChunkSectionLayerMap.putBlock(it, ChunkSectionLayer.CUTOUT)
        }
        ModBlocks.WIDE_CAGE_LAMPS.forEach {
            ChunkSectionLayerMap.putBlock(it, ChunkSectionLayer.CUTOUT)
        }
        ModBlocks.ALARM_CAGE_LAMPS.forEach {
            ChunkSectionLayerMap.putBlock(it, ChunkSectionLayer.CUTOUT)
        }
        ModBlocks.TUBE_LAMPS.forEach {
            ChunkSectionLayerMap.putBlock(it, ChunkSectionLayer.CUTOUT)
        }
    }
}