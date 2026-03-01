package grauly.dustydecor.packet

import com.mojang.serialization.Codec
import grauly.dustydecor.DustyDecorMod
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class ClientboundBlockBreakParticlePayload(val pos: BlockPos, val state: BlockState) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    companion object {
        val IDENTIFIER: Identifier = Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "block_break_particle")
        val ID = CustomPacketPayload.Type<ClientboundBlockBreakParticlePayload>(IDENTIFIER)
        val BLOCK_STATE_CODEC = ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
        val PACKET_CODEC: StreamCodec<ByteBuf, ClientboundBlockBreakParticlePayload> = StreamCodec.composite(
            BlockPos.STREAM_CODEC, { packet: ClientboundBlockBreakParticlePayload -> packet.pos},
            BLOCK_STATE_CODEC, { packet: ClientboundBlockBreakParticlePayload -> packet.state },
            ::ClientboundBlockBreakParticlePayload
        )
    }
}