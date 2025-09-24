package grauly.dustydecor.particle.custom

import grauly.dustydecor.DustyDecorMod
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.state.CameraRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld

abstract class CustomParticle(
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double
) : Particle(world, x, y, z, velocityX, velocityY, velocityZ) {
    override fun textureSheet(): ParticleTextureSheet = CUSTOM_TEXTURE_SHEET

    abstract fun render(queue: OrderedRenderCommandQueue, matrixStack: MatrixStack, camera: CameraRenderState, tickProgress: Float)

    companion object {
        val CUSTOM_TEXTURE_SHEET = ParticleTextureSheet("${DustyDecorMod.MODID}-custom_particle")
    }
}