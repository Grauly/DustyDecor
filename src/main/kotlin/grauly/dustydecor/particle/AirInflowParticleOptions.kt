package grauly.dustydecor.particle

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModParticleTypes
import io.netty.buffer.ByteBuf
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.codec.StreamCodec

class AirInflowParticleOptions(
    val inflowDirection: Direction
) : ParticleOptions {
    override fun getType(): ParticleType<*> = ModParticleTypes.AIR_INFLOW

    companion object {
        val CODEC: MapCodec<AirInflowParticleOptions> = RecordCodecBuilder.mapCodec {
            it.group(
                Direction.CODEC.fieldOf("inflow_direction").forGetter { it.inflowDirection }
            ).apply(it, ::AirInflowParticleOptions)
        }
        val PACKET_CODEC: StreamCodec<ByteBuf, AirInflowParticleOptions> = StreamCodec.composite(
            Direction.STREAM_CODEC,
            AirInflowParticleOptions::inflowDirection,
            ::AirInflowParticleOptions
        )
    }
}