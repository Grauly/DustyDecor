package grauly.dustydecor

import grauly.dustydecor.particle.spark.SparkParticleRenderer
import net.fabricmc.fabric.api.client.particle.v1.ParticleGroupRegistry

object ModParticleGroups {
    fun init() {
        ParticleGroupRegistry.register(SparkParticleRenderer.textureSheet, ::SparkParticleRenderer)
    }
}