package grauly.dustydecor.particle.custom

import net.minecraft.client.particle.ParticleManager
import net.minecraft.client.particle.ParticleRenderer
import net.minecraft.client.render.Camera
import net.minecraft.client.render.Frustum
import net.minecraft.client.render.Submittable
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.state.CameraRenderState
import net.minecraft.client.util.math.MatrixStack

class CustomParticleRenderer(
    particleManager: ParticleManager
) : ParticleRenderer<CustomParticle>(particleManager) {
    override fun render(frustum: Frustum, camera: Camera, tickProgress: Float): Submittable {
        return CustomParticleSubmittable(particles.toList(), tickProgress)
    }

    //TODO: replace with separate data collecting/processing system. Will destroy any idea of generality

    public class CustomParticleSubmittable(
        private val particles: List<CustomParticle>,
        private val tickProgress: Float,
    ): Submittable {
        private val matrixStack: MatrixStack = MatrixStack()

        override fun submit(
            queue: OrderedRenderCommandQueue,
            cameraRenderState: CameraRenderState
        ) {
            particles.forEach {
                it.render(queue, matrixStack, cameraRenderState, tickProgress)
            }
        }
    }
}