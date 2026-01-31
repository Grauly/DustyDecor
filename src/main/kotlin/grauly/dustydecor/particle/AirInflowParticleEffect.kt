package grauly.dustydecor.particle

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModParticleTypes
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.Direction

class AirInflowParticleEffect(
    val inflowDirection: Direction
): ParticleOptions {
    override fun getType(): ParticleType<*> = ModParticleTypes.AIR_INFLOW

    companion object {
        val CODEC: MapCodec<AirInflowParticleEffect> = RecordCodecBuilder.mapCodec {
            it.group(
                Direction.CODEC.fieldOf("inflowDirection").forGetter { it.inflowDirection }
            ).apply(it, ::AirInflowParticleEffect)
        }
        val PACKET_CODEC: StreamCodec<ByteBuf, AirInflowParticleEffect> = StreamCodec.composite(
            Direction.STREAM_CODEC,
            AirInflowParticleEffect::inflowDirection,
            ::AirInflowParticleEffect
        )
    }
}