package grauly.dustydecor.particle

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModParticleTypes
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.util.math.Direction

class AirInflowParticleEffect(
    val outflowDirection: Direction
): ParticleEffect {
    override fun getType(): ParticleType<*> = ModParticleTypes.AIR_INFLOW

    companion object {
        val CODEC: MapCodec<AirInflowParticleEffect> = RecordCodecBuilder.mapCodec {
            it.group(
                Direction.CODEC.fieldOf("outflowDirection").forGetter { it.outflowDirection }
            ).apply(it, ::AirInflowParticleEffect)
        }
        val PACKET_CODEC: PacketCodec<ByteBuf, AirInflowParticleEffect> = PacketCodec.tuple(
            Direction.PACKET_CODEC,
            AirInflowParticleEffect::outflowDirection,
            ::AirInflowParticleEffect
        )
    }
}