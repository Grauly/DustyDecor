package grauly.dustydecor.particle.spark

import grauly.dustydecor.DustyDecorMod
import net.minecraft.client.particle.ParticleEngine
import net.minecraft.client.particle.ParticleGroup
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.Camera
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.state.ParticleGroupRenderState
import net.minecraft.resources.ResourceLocation

class SparkParticleRenderer(particleManager: ParticleEngine) : ParticleGroup<SparkParticle>(particleManager) {
    private val submittable = QuadBasedParticleSubmittable()

    override fun extractRenderState(
        frustum: Frustum,
        camera: Camera,
        tickProgress: Float
    ): ParticleGroupRenderState {
        particles
            .filter { it is SparkParticle }
            .map { it as SparkParticle }
            .filter { it.intersectsFrustum(frustum) }
            .forEach { it.render(submittable, camera, tickProgress) }
        return submittable
    }

    companion object {
        private val id = ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, "spark_renderer").toString()
        val textureSheet = ParticleRenderType(id)
    }
}