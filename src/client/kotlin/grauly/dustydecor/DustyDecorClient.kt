package grauly.dustydecor

import net.fabricmc.api.ClientModInitializer

object DustyDecorClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModBlockLayerRenderMapEntries.init()
        ModColorProviders.init()
    }
}