package grauly.dustydecor.packet

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModComponentTypes
import grauly.dustydecor.ModItems
import grauly.dustydecor.component.BulkGoopSizeComponent
import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.resources.Identifier

class UpdateBulkGoopSizeC2SPacket(val slotId: Int, val size: Int): CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?>? = ID

    fun handle(context: ServerPlayNetworking.Context) {
        val player: ServerPlayer? = context.server().playerList.getPlayer(context.player().uuid)
        if (player == null) return
        val stack = player.inventory.getItem(slotId)
        if (!stack.`is`(ModItems.BULK_VOID_GOOP)) return
        if (!stack.has(ModComponentTypes.VOID_GOOP_SIZE)) return
        if (size > BulkGoopSizeComponent.MAX_SIZE) return
        stack.set(ModComponentTypes.VOID_GOOP_SIZE, BulkGoopSizeComponent(size))
    }

    companion object {
        val IDENTIFIER: Identifier = Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "update_bulk_goop_size")
        val ID = CustomPacketPayload.Type<UpdateBulkGoopSizeC2SPacket>(IDENTIFIER)
        val PACKET_CODEC: StreamCodec<ByteBuf, UpdateBulkGoopSizeC2SPacket> = StreamCodec.composite(
            ByteBufCodecs.INT, { packet: UpdateBulkGoopSizeC2SPacket -> packet.slotId},
            ByteBufCodecs.INT, { packet: UpdateBulkGoopSizeC2SPacket -> packet.size},
            ::UpdateBulkGoopSizeC2SPacket
        )
    }
}