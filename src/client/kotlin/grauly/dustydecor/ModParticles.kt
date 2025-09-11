package grauly.dustydecor

import grauly.dustydecor.particle.LightFlashParticle
import grauly.dustydecor.particle.SparkEmitterFactory
import grauly.dustydecor.particle.SparkFlashParticle
import grauly.dustydecor.particle.SparkParticle
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry

object ModParticles {

    fun init() {
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.SPARK_PARTICLE, SparkParticle::LargeSparkFactory)
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.SMALL_SPARK_PARTICLE, SparkParticle::SmallSparkFactory)
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.SPARK_EMITTER_PARTICLE, ::SparkEmitterFactory)
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.SPARK_FLASH, SparkFlashParticle::Factory)
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.LIGHT_FLASH, LightFlashParticle::Factory)
    }
}