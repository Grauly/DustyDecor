package grauly.dustydecor

import grauly.dustydecor.particle.AirflowParticle
import grauly.dustydecor.particle.LightFlashParticle
import grauly.dustydecor.particle.SparkEmitterFactory
import grauly.dustydecor.particle.SparkFlashParticle
import grauly.dustydecor.particle.spark.SparkParticle
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry

object ModParticles {

    fun init() {
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.SPARK_PARTICLE, SparkParticle::LargeSparkFactory)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.SMALL_SPARK_PARTICLE, SparkParticle::SmallSparkFactory)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.SPARK_EMITTER_PARTICLE, ::SparkEmitterFactory)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.SPARK_FLASH, SparkFlashParticle::Factory)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.LIGHT_FLASH, LightFlashParticle::Factory)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.AIR_INFLOW, AirflowParticle::InflowFactory)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.AIR_OUTFLOW, AirflowParticle::OutflowFactory)
    }
}