package grauly.dustydecor

import grauly.dustydecor.packet.UpdateBulkGoopSizeC2SPacket
import grauly.dustydecor.packet.UpdateVacPipeStationScreenHandlerPropertiesC2SPacket
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object ModServerPacketReceivers {
    fun init() {
        ServerPlayNetworking.registerGlobalReceiver(UpdateBulkGoopSizeC2SPacket.ID, UpdateBulkGoopSizeC2SPacket::handle)
        ServerPlayNetworking.registerGlobalReceiver(
            UpdateVacPipeStationScreenHandlerPropertiesC2SPacket.ID,
            UpdateVacPipeStationScreenHandlerPropertiesC2SPacket::handle
        )
    }
}