package grauly.dustydecor.particle.spark

import grauly.dustydecor.DustyDecorMod
import net.minecraft.client.particle.ParticleManager
import net.minecraft.client.particle.ParticleRenderer
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.render.Camera
import net.minecraft.client.render.Frustum
import net.minecraft.client.render.Submittable
import net.minecraft.util.Identifier

class SparkParticleRenderer(particleManager: ParticleManager) : ParticleRenderer<SparkParticle>(particleManager) {
    private val submittable = SparkParticleSubmittable()

    override fun render(
        frustum: Frustum,
        camera: Camera,
        tickProgress: Float
    ): Submittable {
        particles
            .filter { it is SparkParticle }
            .map { it as SparkParticle }
            .filter { it.intersectsFrustum(frustum) }
            .forEach { it.render(submittable, camera, tickProgress) }
        return submittable
    }

    companion object {
        private val id = Identifier.of(DustyDecorMod.MODID, "spark_renderer").toString()
        val textureSheet = ParticleTextureSheet(id)
    }
}