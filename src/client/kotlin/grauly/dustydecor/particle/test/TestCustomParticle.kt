package grauly.dustydecor.particle.test

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.geometry.PlaneCrossShape
import net.minecraft.client.particle.BillboardParticle
import net.minecraft.client.particle.BillboardParticleRenderer
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.render.Camera
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random

class TestCustomParticle(
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double
) : CustomParticle(world, x, y, z, velocityX, velocityY, velocityZ) {
    override fun render(queue: OrderedRenderCommandQueue, matrixStack: MatrixStack, camera: Camera, tickProgress: Float) {
        DustyDecorMod.logger.info("rendering")
        val pos = Vec3d(x,y,z)
        val camToPos = pos.subtract(camera.pos)
        matrixStack.push()
        matrixStack.translate(camToPos)
        queue.submitCustom(
            matrixStack,
            RenderLayer.getEntityCutout(Identifier.of(DustyDecorMod.MODID, "textures/particle/light_flash.png")),
            { matrices, vertexConsumer ->
                PlaneCrossShape.applyMatrix(matrices, vertexConsumer, Vec2f(0f, 0f), Vec2f(1f,1f)) {
                    it.color(-1).overlay(OverlayTexture.DEFAULT_UV).normal(0f,1f,0f).light(getBrightness(tickProgress))
                }
            }
        )
        matrixStack.pop()
    }

    class Factory(spriteProvider: SpriteProvider): ParticleFactory<SimpleParticleType> {
        override fun createParticle(
            parameters: SimpleParticleType?,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: Random?
        ): Particle {
            return TestCustomParticle(
                world,
                x, y, z, velocityX, velocityY, velocityZ
            )
        }
    }
}