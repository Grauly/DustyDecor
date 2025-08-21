package grauly.dustydecor.particle

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.OptionalFieldCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModParticleTypes
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType

class SparkEmitterParticleEffect(val spread: Double, val amount: Int = 6, val block: Boolean = false) : ParticleEffect {
    override fun getType(): ParticleType<*> = ModParticleTypes.SPARK_EMITTER_PARTICLE_TYPE

    companion object {
        val CODEC: MapCodec<SparkEmitterParticleEffect> = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.DOUBLE.fieldOf("spread_radius").forGetter { spark -> spark.spread },
                Codec.INT.fieldOf("amount").forGetter { spark -> spark.amount },
                Codec.BOOL.fieldOf("block").forGetter { spark -> spark.block }
            ).apply(it, ::SparkEmitterParticleEffect)
        }
        val PACKET_CODEC: PacketCodec<ByteBuf, SparkEmitterParticleEffect> = PacketCodec.tuple(
            PacketCodecs.DOUBLE,
            { spark -> spark.spread },
            PacketCodecs.INTEGER,
            { spark -> spark.amount },
            PacketCodecs.BOOLEAN,
            { spark -> spark.block },
            ::SparkEmitterParticleEffect
        )
    }
}