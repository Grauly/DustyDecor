package grauly.dustydecor

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.particle.SimpleParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModParticleTypes {

    val SPARK_PARTICLE_TYPE: ParticleType<SimpleParticleType> = registerParticle("spark", FabricParticleTypes.simple())
    val SMALL_SPARK_PARTICLE_TYPE: ParticleType<SimpleParticleType> = registerParticle("small_spark", FabricParticleTypes.simple())

    private fun <T: ParticleEffect> registerParticle(id: String, type: ParticleType<T>): ParticleType<T> {
        return registerParticle(Identifier.of(DustyDecorMod.MODID, id), type)
    }

    private fun <T: ParticleEffect> registerParticle(id: Identifier, type: ParticleType<T>): ParticleType<T> {
        return Registry.register(Registries.PARTICLE_TYPE, id, type)
    }

    fun init() {
        //[This space intentionally left blank]
    }
}