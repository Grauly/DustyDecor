package grauly.dustydecor

import net.fabricmc.api.ClientModInitializer

object DustyDecorClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModChunkSectionRenderMapEntries.init()
        ModBlockColors.init()
        ModParticles.init()
        ModBlockEntityRenderers.init()
        ModHandledScreens.init()
        ModParticleRenderers.init()
        ModHudElements.init()
        ModEntityRenderers.init()
    }
}