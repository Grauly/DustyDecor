package grauly.dustydecor.particle.test

import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleTextureSheet
import net.minecraft.client.render.Camera
import net.minecraft.client.render.command.OrderedRenderCommandQueue
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
    open override fun textureSheet(): ParticleTextureSheet = CUSTOM_TEXTURE_SHEET

    abstract fun render(queue: OrderedRenderCommandQueue, matrixStack: MatrixStack, camera: Camera, tickProgress: Float)

    companion object {
        val CUSTOM_TEXTURE_SHEET = ParticleTextureSheet("CUSTOM_PARTICLE")
    }
}