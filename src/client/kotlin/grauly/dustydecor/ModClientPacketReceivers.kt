package grauly.dustydecor

import grauly.dustydecor.network.BlockBreakParticlePayloadHandler
import grauly.dustydecor.packet.ClientboundBlockBreakParticlePayload
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object ModClientPacketReceivers {
    fun init() {
        ClientPlayNetworking.registerGlobalReceiver(
            ClientboundBlockBreakParticlePayload.ID,
            BlockBreakParticlePayloadHandler::handle
        )
    }
}