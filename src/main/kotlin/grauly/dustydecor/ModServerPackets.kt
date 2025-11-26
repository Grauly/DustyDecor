package grauly.dustydecor

import grauly.dustydecor.packet.UpdateBulkGoopSizeC2SPacket
import grauly.dustydecor.packet.UpdateVacPipeStationScreenHandlerPropertiesC2SPacket
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry

object ModServerPackets {
    fun init() {
        PayloadTypeRegistry.playC2S().register(UpdateBulkGoopSizeC2SPacket.ID, UpdateBulkGoopSizeC2SPacket.PACKET_CODEC)
        PayloadTypeRegistry.playC2S().register(
            UpdateVacPipeStationScreenHandlerPropertiesC2SPacket.ID,
            UpdateVacPipeStationScreenHandlerPropertiesC2SPacket.PACKET_CODEC
        )
    }
}