package grauly.dustydecor

import grauly.dustydecor.particle.AirflowParticle
import grauly.dustydecor.particle.LightFlashParticle
import grauly.dustydecor.particle.PhoneRingParticle
import grauly.dustydecor.particle.PhoneRingParticleOptions
import grauly.dustydecor.particle.SparkEmitterProvider
import grauly.dustydecor.particle.SparkFlashParticle
import grauly.dustydecor.particle.spark.SparkParticle
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry

object ModParticles {

    fun init() {
        ParticleProviderRegistry.getInstance()
            .register(ModParticleTypes.SPARK_PARTICLE, SparkParticle::LargeSparkProvider)
        ParticleProviderRegistry.getInstance()
            .register(ModParticleTypes.SMALL_SPARK_PARTICLE, SparkParticle::SmallSparkProvider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.SPARK_EMITTER_PARTICLE, ::SparkEmitterProvider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.SPARK_FLASH, SparkFlashParticle::Provider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.LIGHT_FLASH, LightFlashParticle::Provider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.AIR_INFLOW, AirflowParticle::InflowProvider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.AIR_OUTFLOW, AirflowParticle::OutflowProvider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.PHONE_RING, PhoneRingParticle::Provider)
    }
}