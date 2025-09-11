package grauly.dustydecor

import com.mojang.serialization.MapCodec
import grauly.dustydecor.particle.SparkEmitterParticleEffect
import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.network.codec.PacketCodec
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.particle.SimpleParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModParticleTypes {

    val SPARK_PARTICLE: SimpleParticleType = registerSimple("spark")
    val SMALL_SPARK_PARTICLE: SimpleParticleType = registerSimple("small_spark")
    val SPARK_FLASH: SimpleParticleType = registerSimple("spark_flash")
    val SPARK_EMITTER_PARTICLE: ParticleType<SparkEmitterParticleEffect> = registerComplex("spark_emitter", SparkEmitterParticleEffect.CODEC, SparkEmitterParticleEffect.PACKET_CODEC)
    val LIGHT_FLASH: SimpleParticleType = registerSimple("light_flash")
    val TEST_PARTICLE: SimpleParticleType = registerSimple("test")

    private fun <T: ParticleEffect> registerParticle(id: String, type: ParticleType<T>): ParticleType<T> {
        return registerParticle(Identifier.of(DustyDecorMod.MODID, id), type)
    }

    private fun <T: ParticleEffect> registerParticle(id: Identifier, type: ParticleType<T>): ParticleType<T> {
        return Registry.register(Registries.PARTICLE_TYPE, id, type)
    }

    private fun <T: ParticleEffect> registerComplex(
        id: String,
        mapCodec: MapCodec<T>,
        packetCodec: PacketCodec<ByteBuf, T>,
        alwaysShow: Boolean = false
    ): ParticleType<T> {
        return registerParticle(id, FabricParticleTypes.complex(alwaysShow, mapCodec, packetCodec))
    }

    private fun registerSimple(id: String, showAlways: Boolean = false): SimpleParticleType {
        return Registry.register(
            Registries.PARTICLE_TYPE,
            Identifier.of(DustyDecorMod.MODID, id),
            FabricParticleTypes.simple(showAlways)
        )
    }

    fun init() {
        //[This space intentionally left blank]
    }
}