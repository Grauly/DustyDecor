package grauly.dustydecor.packet

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.screen.VacPipeStationScreenHandler
import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import java.lang.IndexOutOfBoundsException

class UpdateVacPipeStationScreenHandlerPropertiesC2SPacket(val syncId: Int, val property: Int, val value: Int): CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload?>? = ID

    fun handle(context: ServerPlayNetworking.Context) {
        val currentHandler = context.player().currentScreenHandler
        if (currentHandler.syncId != syncId) return
        if (currentHandler !is VacPipeStationScreenHandler<*>) return
        try {
            currentHandler.setProperty(property, value)
            currentHandler.sendContentUpdates()
            DustyDecorMod.logger.info("[VacPipeStationScreenHandler] updated property")
        } catch (e: IndexOutOfBoundsException) {
            DustyDecorMod.logger.warn("Player with UUID: ${context.player().uuidAsString} (${context.player().name}) sent a UpdateVacPipeStationScreenHandlerPropertiesC2SPacket with invalid property index: $property", e)
        } catch (e: kotlin.IndexOutOfBoundsException) {
            DustyDecorMod.logger.warn("Player with UUID: ${context.player().uuidAsString} (${context.player().name}) sent a UpdateVacPipeStationScreenHandlerPropertiesC2SPacket with invalid property value: $value", e)
        }
    }

    companion object {
        val IDENTIFER: Identifier = Identifier.of(DustyDecorMod.MODID, "update_vac_pipe_station_screen_handler_properties")
        val ID = CustomPayload.Id<UpdateVacPipeStationScreenHandlerPropertiesC2SPacket>(IDENTIFER)
        val PACKET_CODEC: PacketCodec<ByteBuf, UpdateVacPipeStationScreenHandlerPropertiesC2SPacket> = PacketCodec.tuple(
            PacketCodecs.INTEGER, {packet: UpdateVacPipeStationScreenHandlerPropertiesC2SPacket -> packet.syncId},
            PacketCodecs.INTEGER, {packet: UpdateVacPipeStationScreenHandlerPropertiesC2SPacket -> packet.property},
            PacketCodecs.INTEGER, {packet: UpdateVacPipeStationScreenHandlerPropertiesC2SPacket -> packet.value},
            ::UpdateVacPipeStationScreenHandlerPropertiesC2SPacket
        )
    }

}