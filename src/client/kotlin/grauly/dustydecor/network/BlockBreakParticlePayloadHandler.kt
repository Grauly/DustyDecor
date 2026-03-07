package grauly.dustydecor.network

import grauly.dustydecor.packet.ClientboundBlockBreakParticlePayload
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object BlockBreakParticlePayloadHandler {
    fun handle(packet: ClientboundBlockBreakParticlePayload, context: ClientPlayNetworking.Context) {
        context.client().level?.addDestroyBlockEffect(packet.pos, packet.state)
    }
}