package grauly.dustydecor

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import grauly.dustydecor.particle.SparkEmitterParticleEffect
import io.netty.buffer.ByteBuf
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModParticleTypes {

    val SPARK_PARTICLE: SimpleParticleType = registerSimple("spark")
    val SMALL_SPARK_PARTICLE: SimpleParticleType = registerSimple("small_spark")
    val SPARK_FLASH: SimpleParticleType = registerSimple("spark_flash")
    val SPARK_EMITTER_PARTICLE: ParticleType<SparkEmitterParticleEffect> = registerComplex("spark_emitter", SparkEmitterParticleEffect.CODEC, SparkEmitterParticleEffect.PACKET_CODEC)
    val LIGHT_FLASH: SimpleParticleType = registerSimple("light_flash")

    private fun <T: ParticleEffect> registerParticle(id: String, type: ParticleType<T>): ParticleType<T> {
        return registerParticle(Identifier.of(DustyDecorMod.MODID, id), type)
    }

    private fun <T: ParticleEffect> registerParticle(id: Identifier, type: ParticleType<T>): ParticleType<T> {
        return Registry.register(Registries.PARTICLE_TYPE, id, type)
    }

    //TODO: replace with proper FAPI versions once the exist
    private fun <T: ParticleEffect> registerComplex(id: String, mapCodec: MapCodec<T>, packetCodec: PacketCodec<ByteBuf, T>, alwaysShow: Boolean = false): ParticleType<T> {
        val type = object : ParticleType<T>(alwaysShow) {
            override fun getCodec(): MapCodec<T> = mapCodec
            override fun getPacketCodec(): PacketCodec<in RegistryByteBuf, T> = packetCodec
        }
        return registerParticle(id, type)
    }

    private fun registerSimple(id: String, showAlways: Boolean = false): SimpleParticleType {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(DustyDecorMod.MODID, id), SimpleParticleType(showAlways))
    }

    fun init() {
        //[This space intentionally left blank]
    }

    class SimpleParticleType(showAlways: Boolean): net.minecraft.particle.SimpleParticleType(showAlways)
}