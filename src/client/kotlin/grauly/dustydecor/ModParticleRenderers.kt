package grauly.dustydecor

import grauly.dustydecor.particle.custom.CustomParticle
import grauly.dustydecor.particle.custom.CustomParticleRenderer
import net.fabricmc.fabric.api.client.particle.v1.ParticleRendererRegistry

object ModParticleRenderers {
    fun init() {
        ParticleRendererRegistry.register(CustomParticle.CUSTOM_TEXTURE_SHEET, ::CustomParticleRenderer)
    }
}