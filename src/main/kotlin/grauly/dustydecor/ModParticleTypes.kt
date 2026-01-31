package grauly.dustydecor

import com.mojang.serialization.MapCodec
import grauly.dustydecor.particle.AirInflowParticleEffect
import grauly.dustydecor.particle.AirOutflowParticleEffect
import grauly.dustydecor.particle.SparkEmitterParticleEffect
import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.network.codec.StreamCodec
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry
import net.minecraft.resources.Identifier

object ModParticleTypes {

    val SPARK_PARTICLE: SimpleParticleType = registerSimple("spark")
    val SMALL_SPARK_PARTICLE: SimpleParticleType = registerSimple("small_spark")
    val SPARK_FLASH: SimpleParticleType = registerSimple("spark_flash")
    val LIGHT_FLASH: SimpleParticleType = registerSimple("light_flash")

    val SPARK_EMITTER_PARTICLE: ParticleType<SparkEmitterParticleEffect> = registerComplex(
        "spark_emitter",
        SparkEmitterParticleEffect.CODEC,
        SparkEmitterParticleEffect.PACKET_CODEC
    )
    val AIR_INFLOW: ParticleType<AirInflowParticleEffect> = registerComplex(
        "air_inflow",
        AirInflowParticleEffect.CODEC,
        AirInflowParticleEffect.PACKET_CODEC
    )
    val AIR_OUTFLOW: ParticleType<AirOutflowParticleEffect> = registerComplex(
        "air_outflow",
        AirOutflowParticleEffect.CODEC,
        AirOutflowParticleEffect.PACKET_CODEC
    )

    private fun <T: ParticleOptions> registerParticle(id: String, type: ParticleType<T>): ParticleType<T> {
        return registerParticle(Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, id), type)
    }

    private fun <T: ParticleOptions> registerParticle(id: Identifier, type: ParticleType<T>): ParticleType<T> {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, id, type)
    }

    private fun <T: ParticleOptions> registerComplex(
        id: String,
        mapCodec: MapCodec<T>,
        packetCodec: StreamCodec<ByteBuf, T>,
        alwaysShow: Boolean = false
    ): ParticleType<T> {
        return registerParticle(id, FabricParticleTypes.complex(alwaysShow, mapCodec, packetCodec))
    }

    private fun registerSimple(id: String, showAlways: Boolean = false): SimpleParticleType {
        return Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, id),
            FabricParticleTypes.simple(showAlways)
        )
    }

    fun init() {
        //[This space intentionally left blank]
    }
}