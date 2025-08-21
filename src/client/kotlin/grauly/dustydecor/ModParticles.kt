package grauly.dustydecor

import grauly.dustydecor.particle.SparkEmitterParticle
import grauly.dustydecor.particle.SparkParticle
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry

object ModParticles {

    fun init() {
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.SPARK_PARTICLE_TYPE, SparkParticle::LargeSparkFactory)
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.SMALL_SPARK_PARTICLE_TYPE, SparkParticle::SmallSparkFactory)
        ParticleFactoryRegistry.getInstance().register(ModParticleTypes.SPARK_EMITTER_PARTICLE_TYPE, SparkEmitterParticle::Factory)
    }
}