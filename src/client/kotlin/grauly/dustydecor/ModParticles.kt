package grauly.dustydecor

import grauly.dustydecor.particle.AirflowParticle
import grauly.dustydecor.particle.LightFlashParticle
import grauly.dustydecor.particle.MetalSparkParticle
import grauly.dustydecor.particle.NonMovingVelocityPointingParticle
import grauly.dustydecor.particle.OutsideShockwaveParticle
import grauly.dustydecor.particle.OutsideSparkParticle
import grauly.dustydecor.particle.VelocityPointingParticle
import grauly.dustydecor.particle.PhoneRingParticle
import grauly.dustydecor.particle.SparkEmitterProvider
import grauly.dustydecor.particle.SparkFlashParticle
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry

object ModParticles {

    fun init() {
        ParticleProviderRegistry.getInstance()
            .register(ModParticleTypes.SPARK_PARTICLE, MetalSparkParticle::LargeSparkProvider)
        ParticleProviderRegistry.getInstance()
            .register(ModParticleTypes.SMALL_SPARK_PARTICLE, MetalSparkParticle::SmallSparkProvider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.SPARK_EMITTER_PARTICLE, ::SparkEmitterProvider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.SPARK_FLASH, SparkFlashParticle::Provider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.LIGHT_FLASH, LightFlashParticle::Provider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.AIR_INFLOW, AirflowParticle::InflowProvider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.AIR_OUTFLOW, AirflowParticle::OutflowProvider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.PHONE_RING, PhoneRingParticle::Provider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.OUTSIDE_SPARK, OutsideSparkParticle::SmallSparkProvider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.OUTSIDE_SPARKLET, VelocityPointingParticle::Provider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.OUTSIDE_BEAM_FLASH, NonMovingVelocityPointingParticle::Provider)
        ParticleProviderRegistry.getInstance().register(ModParticleTypes.OUTSIDE_SHOCKWAVE, OutsideShockwaveParticle::Provider)
    }
}