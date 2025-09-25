package grauly.dustydecor

import grauly.dustydecor.particle.spark.SparkParticleRenderer
import net.fabricmc.fabric.api.client.particle.v1.ParticleRendererRegistry

object ModParticleRenderers {
    fun init() {
        ParticleRendererRegistry.register(SparkParticleRenderer.textureSheet, ::SparkParticleRenderer)
    }
}