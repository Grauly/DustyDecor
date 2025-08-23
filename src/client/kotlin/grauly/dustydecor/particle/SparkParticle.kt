package grauly.dustydecor.particle

import net.minecraft.client.particle.*
import net.minecraft.client.render.Camera
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.Colors
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.LightType
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.roundToInt

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
    lifetime: Int,
    drag: Double = 1.0,
    private val lengthFactor: Float = 4f,
    sparkWidthPixels: Double = 1.0,
    private val spriteProvider: SpriteProvider
) :
    SpriteBillboardParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ) {
    private var lastLastX = 0.0
    private var lastLastY = 0.0
    private var lastLastZ = 0.0

    private val bounceFactor = 0.8
    private val sparkWidth: Double = sparkWidthPixels / 16

    init {
        this.gravityStrength = gravity.toFloat()
        this.velocityMultiplier = drag.toFloat()
        this.velocityX = velocityX
        this.velocityY = velocityY
        this.velocityZ = velocityZ
        maxAge = lifetime
        lastLastX = lastX
        lastLastY = lastY
        lastLastZ = lastZ
        setSprite(spriteProvider.getSprite(age, maxAge))
        scale = 0.9f + random.nextFloat() * 0.2f
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
        setSprite(spriteProvider.getSprite(age, maxAge))
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
        renderParticle(renderLocalPos, renderLocalLastPos, vertexConsumer, tickProgress)
    }

    private fun renderParticle(
        renderLocalPos: Vec3d,
        renderLocalLastPos: Vec3d,
        vertexConsumer: VertexConsumer,
        tickProgress: Float
    ) {
        val centerPos = renderLocalLastPos.lerp(renderLocalPos, 0.5)
        val spanVector = renderLocalPos.subtract(renderLocalLastPos)
        val speedSquared = spanVector.lengthSquared()
        val scaleVector = Vector3f(
            max(lengthFactor * speedSquared.toFloat(), getSize(tickProgress)),
            getSize(tickProgress),
            getSize(tickProgress)
        )
        val rotation = if (spanVector.lengthSquared() != 0.0) {
            Quaternionf().rotationTo(Vector3f(1f, 0f, 0f), spanVector.normalize().toVector3f())
        } else {
            Quaternionf()
        }

        renderPlaneParticle(
            centerPos,
            scaleVector,
            rotation,
            vertexConsumer,
            tickProgress
        )
    }

    private fun renderPlaneParticle(
        centerPos: Vec3d,
        scaleVector: Vector3f,
        rotation: Quaternionf,
        vertexConsumer: VertexConsumer,
        tickProgress: Float
    ) {
        val baseVector = Vector3f(scaleVector.x, 0f, 0f).rotate(rotation).mul(0.5f)
        renderParticle(
            centerPos.toVector3f().add(baseVector),
            centerPos.toVector3f().add(baseVector.negate()),
            vertexConsumer,
            tickProgress
        )
    }


    private fun renderParticle(
        from: Vector3f,
        to: Vector3f,
        vertexConsumer: VertexConsumer,
        tickProgress: Float
    ) {
        val side = Vector3f(to).sub(from).normalize()
        val camForward = Vector3f(to).lerp(from, 0.5f)
        val sparkUp = Vector3f(side).cross(camForward).normalize().mul((getSize(tickProgress) / 2))
        val light = getBrightness(0f)

        vertexConsumer.vertex(Vector3f(to).add(Vector3f(sparkUp).negate())).light(light).color(Colors.WHITE)
            .texture(minU, maxV)
        vertexConsumer.vertex(Vector3f(to).add(sparkUp)).light(light).color(Colors.WHITE).texture(minU, minV)
        vertexConsumer.vertex(Vector3f(from).add(sparkUp)).light(light).color(Colors.WHITE).texture(maxU, minV)
        vertexConsumer.vertex(Vector3f(from).add(Vector3f(sparkUp).negate())).light(light).color(Colors.WHITE)
            .texture(maxU, maxV)
    }

    override fun getBrightness(tint: Float): Int {
        val sparkBrightness: Int = ((age.toFloat() / maxAge) * 15).roundToInt()
        val pos: BlockPos = BlockPos.ofFloored(x, y, z)
        val blockLight: Int = world.getLightLevel(LightType.BLOCK, pos)
        val skyLight: Int = world.getLightLevel(LightType.SKY, pos)
        return LightmapTextureManager.pack(max(sparkBrightness, blockLight), skyLight)
    }

    override fun getSize(tickProgress: Float): Float {
        return sparkWidth.toFloat() * scale * (1f - (age + tickProgress) / maxAge)
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_OPAQUE

    class LargeSparkFactory(private val spriteProvider: SpriteProvider) : ParticleFactory<SimpleParticleType> {
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
            return SparkParticle(
                world,
                x,
                y,
                z,
                velocityX,
                velocityY,
                velocityZ,
                randomDoubleBetween(world.random, 2.3, 2.4),
                world.random.nextInt(10) + 50,
                lengthFactor = 2.5f,
                spriteProvider = spriteProvider
            )
        }
    }

    class SmallSparkFactory(private val spriteProvider: SpriteProvider) : ParticleFactory<SimpleParticleType> {
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
            return SparkParticle(
                world,
                x,
                y,
                z,
                velocityX,
                velocityY,
                velocityZ,
                randomDoubleBetween(world.random, 1.2, 1.3),
                world.random.nextInt(5) + 25,
                lengthFactor = 3.5f,
                spriteProvider = spriteProvider
            )
        }
    }

    companion object {
        private fun randomDoubleBetween(random: Random, start: Double, end: Double): Double {
            val base = random.nextDouble()
            val diff = end - start
            return start + (base * diff)
        }
    }
}