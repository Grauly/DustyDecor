package grauly.dustydecor

import grauly.dustydecor.particle.SparkEmitterParticleEffect
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.particle.SimpleParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModParticleTypes {

    val SPARK_PARTICLE: SimpleParticleType = registerSimple("spark", FabricParticleTypes.simple())
    val SMALL_SPARK_PARTICLE: SimpleParticleType = registerSimple("small_spark", FabricParticleTypes.simple())
    val SPARK_FLASH: SimpleParticleType = registerSimple("spark_flash", FabricParticleTypes.simple())
    val SPARK_EMITTER_PARTICLE: ParticleType<SparkEmitterParticleEffect> = registerParticle("spark_emitter", FabricParticleTypes.complex(SparkEmitterParticleEffect.CODEC, SparkEmitterParticleEffect.PACKET_CODEC))
    val LIGHT_FLASH: SimpleParticleType = registerSimple("light_flash", FabricParticleTypes.simple())

    private fun <T: ParticleEffect> registerParticle(id: String, type: ParticleType<T>): ParticleType<T> {
        return registerParticle(Identifier.of(DustyDecorMod.MODID, id), type)
    }

    private fun <T: ParticleEffect> registerParticle(id: Identifier, type: ParticleType<T>): ParticleType<T> {
        return Registry.register(Registries.PARTICLE_TYPE, id, type)
    }

    private fun registerSimple(id: String, type: SimpleParticleType): SimpleParticleType {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(DustyDecorMod.MODID, id), type)
    }

    fun init() {
        //[This space intentionally left blank]
    }
}