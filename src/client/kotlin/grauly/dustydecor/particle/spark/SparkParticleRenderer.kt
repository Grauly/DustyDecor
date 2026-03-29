package grauly.dustydecor.particle.spark

import grauly.dustydecor.DustyDecorMod
import net.minecraft.client.Camera
import net.minecraft.client.particle.ParticleEngine
import net.minecraft.client.particle.ParticleGroup
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState
import net.minecraft.resources.Identifier

class SparkParticleRenderer(particleEngine: ParticleEngine) : ParticleGroup<SparkParticle>(particleEngine) {
    private val renderState = QuadBasedRenderState()

    override fun extractRenderState(
        frustum: Frustum,
        camera: Camera,
        tickProgress: Float
    ): ParticleGroupRenderState {
        particles
            .filter { it is SparkParticle }
            .map { it as SparkParticle }
            .filter { it.intersectsFrustum(frustum) }
            .forEach { it.render(renderState, camera, tickProgress) }
        return renderState
    }

    companion object {
        private val id = Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "spark_renderer").toString()
        val textureSheet = ParticleRenderType(id)
    }
}