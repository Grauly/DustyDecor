package grauly.dustydecor.packet

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModComponentTypes
import grauly.dustydecor.ModItems
import grauly.dustydecor.component.BulkGoopSizeComponent
import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class UpdateBulkGoopSizeC2SPacket(val slotId: Int, val size: Int): CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload?>? = ID

    fun handle(context: ServerPlayNetworking.Context) {
        val player: ServerPlayerEntity? = context.server().playerManager.getPlayer(context.player().uuid)
        if (player == null) return
        val stack = player.inventory.getStack(slotId)
        if (!stack.isOf(ModItems.BULK_VOID_GOOP)) return
        if (!stack.contains(ModComponentTypes.VOID_GOOP_SIZE)) return
        if (size > BulkGoopSizeComponent.MAX_SIZE) return
        stack.set(ModComponentTypes.VOID_GOOP_SIZE, BulkGoopSizeComponent(size))
    }

    companion object {
        val IDENTIFIER: Identifier = Identifier.of(DustyDecorMod.MODID, "update_bulk_goop_size")
        val ID = CustomPayload.Id<UpdateBulkGoopSizeC2SPacket>(IDENTIFIER)
        val PACKET_CODEC: PacketCodec<ByteBuf, UpdateBulkGoopSizeC2SPacket> = PacketCodec.tuple(
            PacketCodecs.INTEGER, {packet: UpdateBulkGoopSizeC2SPacket -> packet.slotId},
            PacketCodecs.INTEGER, {packet: UpdateBulkGoopSizeC2SPacket -> packet.size},
            ::UpdateBulkGoopSizeC2SPacket
        )
    }
}