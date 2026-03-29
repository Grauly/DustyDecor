package grauly.dustydecor.particle

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModParticleTypes
import io.netty.buffer.ByteBuf
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

class PhoneRingParticleOptions(
    val attached: Boolean = false,
    val flipped: Boolean = false,
) : ParticleOptions {
    override fun getType(): ParticleType<*> = ModParticleTypes.PHONE_RING

    companion object {
        val CODEC: MapCodec<PhoneRingParticleOptions> = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.BOOL.fieldOf("attached").forGetter { it.attached },
                Codec.BOOL.fieldOf("flipped").forGetter { it.flipped },
            ).apply(it, ::PhoneRingParticleOptions)
        }
        val PACKET_CODEC: StreamCodec<ByteBuf, PhoneRingParticleOptions> = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            PhoneRingParticleOptions::attached,
            ByteBufCodecs.BOOL,
            PhoneRingParticleOptions::flipped,
            ::PhoneRingParticleOptions
        )
    }
}