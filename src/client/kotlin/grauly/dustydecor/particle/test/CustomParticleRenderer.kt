package grauly.dustydecor.particle.test

import grauly.dustydecor.DustyDecorMod
import net.minecraft.client.particle.ParticleManager
import net.minecraft.client.particle.ParticleRenderer
import net.minecraft.client.render.Camera
import net.minecraft.client.render.Frustum
import net.minecraft.client.render.Submittable
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.util.math.MatrixStack

class CustomParticleRenderer(
    particleManager: ParticleManager
) : ParticleRenderer<CustomParticle>(particleManager) {
    override fun render(frustum: Frustum, camera: Camera, tickProgress: Float): Submittable {
        return CustomParticleSubmittable(particles.toList(), camera, tickProgress)
    }

    public class CustomParticleSubmittable(
        private val particles: List<CustomParticle>,
        private val camera: Camera,
        private val tickProgress: Float,
    ): Submittable {
        init {
            DustyDecorMod.logger.info("creating submitter")
        }
        private val matrixStack: MatrixStack = MatrixStack()
        override fun submit(queue: OrderedRenderCommandQueue) {
            DustyDecorMod.logger.info("submitting")
            particles.forEach {
                it.render(queue, matrixStack, camera, tickProgress)
            }
        }
    }
}