package grauly.dustydecor

import grauly.dustydecor.packet.ClientboundBlockBreakParticlePayload
import grauly.dustydecor.packet.UpdateBulkGoopSizeC2SPacket
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry

object ModServerPackets {
    fun init() {
        PayloadTypeRegistry.serverboundPlay().register(UpdateBulkGoopSizeC2SPacket.ID, UpdateBulkGoopSizeC2SPacket.PACKET_CODEC)
        PayloadTypeRegistry.clientboundPlay().register(
            ClientboundBlockBreakParticlePayload.ID,
            ClientboundBlockBreakParticlePayload.PACKET_CODEC
        )
    }
}