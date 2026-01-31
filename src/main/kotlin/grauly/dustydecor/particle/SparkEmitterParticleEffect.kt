package grauly.dustydecor.particle

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModParticleTypes
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType

class SparkEmitterParticleEffect(val spread: Double, val amount: Int = 6, val block: Boolean = false) :
    ParticleOptions {
    override fun getType(): ParticleType<*> = ModParticleTypes.SPARK_EMITTER_PARTICLE

    companion object {
        val CODEC: MapCodec<SparkEmitterParticleEffect> = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.DOUBLE.fieldOf("spread_radius").forGetter { spark -> spark.spread },
                Codec.INT.fieldOf("amount").forGetter { spark -> spark.amount },
                Codec.BOOL.fieldOf("block").forGetter { spark -> spark.block }
            ).apply(it, ::SparkEmitterParticleEffect)
        }
        val PACKET_CODEC: StreamCodec<ByteBuf, SparkEmitterParticleEffect> = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            { spark -> spark.spread },
            ByteBufCodecs.INT,
            { spark -> spark.amount },
            ByteBufCodecs.BOOL,
            { spark -> spark.block },
            ::SparkEmitterParticleEffect
        )
    }
}