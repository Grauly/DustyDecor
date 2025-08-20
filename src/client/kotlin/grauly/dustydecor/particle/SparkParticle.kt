package grauly.dustydecor.particle

import com.mojang.blaze3d.systems.RenderSystem
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.geometry.BiPlaneShape
import grauly.dustydecor.geometry.PlaneCrossShape
import grauly.dustydecor.geometry.PlaneShape
import net.minecraft.client.particle.*
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexRendering
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.Colors
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import org.joml.Quaternionf
import org.joml.Vector3f

/**
 * Implemented with very generous lookups from https://github.com/Enchanted-Games/block-place-particles
 * Honestly, without the showcase on the fabricord, I would not have had this idea, so ty :)
 */
class SparkParticle(
    clientWorld: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    gravity: Double,
    drag: Double,
    spriteProvider: SpriteProvider
) :
    SpriteBillboardParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ) {
    private var lastLastX = 0.0
    private var lastLastY = 0.0
    private var lastLastZ = 0.0

    init {
        this.gravityStrength = gravity.toFloat()
        this.velocityMultiplier = drag.toFloat()
        this.velocityX = velocityX
        this.velocityY = velocityY
        this.velocityZ = velocityZ
        maxAge = 200
        lastLastX = lastX
        lastLastY = lastY
        lastLastZ = lastZ
        setSprite(spriteProvider)
    }

    override fun tick() {
        lastLastX = lastX
        lastLastY = lastY
        lastLastZ = lastZ

        val velocity = Vec3d(velocityX, velocityY, velocityZ)
        val collision = Entity.adjustMovementForCollisions(null, velocity, boundingBox, world, listOf())
        velocityX = if (collision.x == 0.0) -velocityX * bounceFactor else collision.x
        velocityY = if (collision.y == 0.0) -velocityY * bounceFactor else collision.y
        velocityZ = if (collision.z == 0.0) -velocityZ * bounceFactor else collision.z
        super.tick()
    }

    override fun render(
        vertexConsumer: VertexConsumer,
        camera: Camera,
        quaternionf: Quaternionf,
        tickProgress: Float
    ) {
        val renderLocalPos =
            Vec3d(lastX, lastY, lastZ).lerp(Vec3d(x, y, z), tickProgress.toDouble()).subtract(camera.pos)
        val renderLocalLastPos =
            Vec3d(lastLastX, lastLastY, lastLastZ).lerp(Vec3d(lastX, lastY, lastZ), tickProgress.toDouble())
                .subtract(camera.pos)
        val spanVector = renderLocalPos.subtract(renderLocalLastPos)
        val centerPos = renderLocalLastPos.lerp(renderLocalPos, 0.5)
        val scaleVector = Vec3d(spanVector.length(), 1.0 / 16, 1.0 / 16)
        val rotation = Quaternionf().rotationTo(Vector3f(1f, 0f, 0f), spanVector.normalize().toVector3f())
        val minUv = Vec2f(minU, minV)
        val maxUv = Vec2f(maxU, maxV)

        PlaneCrossShape
            .getTransformed(centerPos, scaleVector, rotation)
            .apply(vertexConsumer, getBrightness(tickProgress), Colors.WHITE, minUv, maxUv)
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_OPAQUE

    class Factory(private val spriteProvider: SpriteProvider) : ParticleFactory<SimpleParticleType> {
        override fun createParticle(
            parameters: SimpleParticleType?,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ): Particle {
            return SparkParticle(world, x, y, z, velocityX, velocityY, velocityZ, randomDoubleBetween(world.random, 0.2, 0.3), 1.0, spriteProvider)
        }

        private fun randomDoubleBetween(random: Random, start: Double, end: Double): Double {
            val base = random.nextDouble()
            val diff = end - start
            return start + (base * diff)
        }
    }


    companion object {
        private const val bounceFactor = 0.6
    }
}