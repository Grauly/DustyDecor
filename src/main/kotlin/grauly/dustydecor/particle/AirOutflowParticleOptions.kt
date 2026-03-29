package grauly.dustydecor.particle

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModParticleTypes
import io.netty.buffer.ByteBuf
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.codec.StreamCodec

class AirOutflowParticleOptions(
    val outflowDirection: Direction
) : ParticleOptions {
    override fun getType(): ParticleType<*> = ModParticleTypes.AIR_OUTFLOW

    companion object {
        val CODEC: MapCodec<AirOutflowParticleOptions> = RecordCodecBuilder.mapCodec {
            it.group(
                Direction.CODEC.fieldOf("outflowDirection").forGetter { it.outflowDirection }
            ).apply(it, ::AirOutflowParticleOptions)
        }
        val PACKET_CODEC: StreamCodec<ByteBuf, AirOutflowParticleOptions> = StreamCodec.composite(
            Direction.STREAM_CODEC,
            AirOutflowParticleOptions::outflowDirection,
            ::AirOutflowParticleOptions
        )
    }
}