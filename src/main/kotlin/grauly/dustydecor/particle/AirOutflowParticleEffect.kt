package grauly.dustydecor.particle

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModParticleTypes
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.util.math.Direction

class AirOutflowParticleEffect(
    val outflowDirection: Direction
): ParticleEffect {
    override fun getType(): ParticleType<*> = ModParticleTypes.AIR_OUTFLOW

    companion object {
        val CODEC: MapCodec<AirOutflowParticleEffect> = RecordCodecBuilder.mapCodec {
            it.group(
                Direction.CODEC.fieldOf("outflowDirection").forGetter { it.outflowDirection }
            ).apply(it, ::AirOutflowParticleEffect)
        }
        val PACKET_CODEC: PacketCodec<ByteBuf, AirOutflowParticleEffect> = PacketCodec.tuple(
            Direction.PACKET_CODEC,
            AirOutflowParticleEffect::outflowDirection,
            ::AirOutflowParticleEffect
        )
    }
}