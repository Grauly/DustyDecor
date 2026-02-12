package grauly.dustydecor

import net.fabricmc.api.ClientModInitializer

object DustyDecorClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModBlockColors.init()
        ModParticles.init()
        ModBlockEntityRenderers.init()
        ModHandledScreens.init()
        ModParticleGroups.init()
        ModHudElements.init()
        ModEntityRenderers.init()
    }
}