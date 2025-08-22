package grauly.dustydecor.particle

import com.mojang.blaze3d.systems.RenderSystem
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.geometry.BiPlaneShape
import grauly.dustydecor.geometry.PlaneCrossShape
import grauly.dustydecor.geometry.PlaneShape
import net.minecraft.client.MinecraftClient
import net.minecraft.client.particle.*
import net.minecraft.client.render.Camera
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexRendering
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.particle.SimpleParticleType
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RotationCalculator
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.LightType
import org.joml.Quaternionf
import org.joml.Vector3f
import java.text.NumberFormat
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow
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
    private var lastSpanVector = Vec3d(velocityX, velocityY, velocityZ)

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
        //super.render(vertexConsumer, camera, quaternionf, tickProgress)
        renderParticle(renderLocalPos, renderLocalLastPos, camera, vertexConsumer)
    }

    private fun renderParticle(
        renderLocalPos: Vec3d,
        renderLocalLastPos: Vec3d,
        camera: Camera,
        vertexConsumer: VertexConsumer
    ) {
        val centerPos = renderLocalLastPos.lerp(renderLocalPos, 0.5)
        val spanVector = renderLocalPos.subtract(renderLocalLastPos)
        val speedSquared = spanVector.lengthSquared()
        val scaleVector = Vec3d(max(lengthFactor * speedSquared, sparkWidth), sparkWidth, sparkWidth)
        val rotation = Quaternionf().rotationTo(Vector3f(1f, 0f, 0f), spanVector.normalize().toVector3f())
        val minUv = Vec2f(minU, minV)
        val maxUv = Vec2f(maxU, maxV)

        renderPlaneParticle(
            centerPos,
            scaleVector,
            rotation,
            vertexConsumer,
            camera
        )
    }

    private fun renderCrossParticle(
        centerPos: Vec3d,
        scaleVector: Vec3d,
        rotation: Quaternionf,
        vertexConsumer: VertexConsumer,
        camera: Camera? = null,
    ) {
        PlaneCrossShape
            .getTransformed(centerPos, scaleVector, rotation)
            .apply(vertexConsumer, getBrightness(0f), -1, Vec2f(minU, minV), Vec2f(maxU, maxV))
    }

    private fun renderPlaneParticle(
        centerPos: Vec3d,
        scaleVector: Vec3d,
        rotation: Quaternionf,
        vertexConsumer: VertexConsumer,
        camera: Camera
    ) {
        val baseVector = Vector3f(scaleVector.x.toFloat(), 0f, 0f).rotate(rotation).mul(0.5f)
        renderParticle(centerPos.toVector3f().add(baseVector), centerPos.toVector3f().add(baseVector.negate()), camera, vertexConsumer)
/*
        val camRot = Quaternionf().rotateX((PI/2 - Math.toRadians(camera.pitch.toDouble())).toFloat())
        PlaneShape
            .getTransformed(centerPos, scaleVector, camRot.mul(rotation))
            .apply(vertexConsumer, getBrightness(0f), -1, Vec2f(minU, minV), Vec2f(maxU, maxV))
*/
    }

    private fun renderSimplerParticle(
        renderLocalPos: Vec3d,
        renderLocalLastPos: Vec3d,
        camera: Camera,
        vertexConsumer: VertexConsumer
    ) {
        val centerPos = renderLocalLastPos.lerp(renderLocalPos, 0.5)
        var spanVector = renderLocalPos.subtract(renderLocalLastPos)
        if (spanVector.lengthSquared() < sparkWidth.pow(2)) {
            spanVector = spanVector.normalize().multiply(sparkWidth)
        }
        if (!spanVector.x.isFinite() || !spanVector.y.isFinite() || !spanVector.z.isFinite()) {
            spanVector = this.lastSpanVector
        }
        val speedSquared = spanVector.lengthSquared()
        val partialTargetLength = speedSquared * lengthFactor / 2
        val cToPos = renderLocalPos.subtract(centerPos).normalize().multiply(partialTargetLength)
        val cToLastPos = renderLocalLastPos.subtract(centerPos).normalize().multiply(partialTargetLength)
        renderParticle(cToLastPos.toVector3f(), cToPos.toVector3f(), camera, vertexConsumer)
        this.lastSpanVector = spanVector
    }

    private fun renderParticle(from: Vector3f, to: Vector3f, camera: Camera, vertexConsumer: VertexConsumer) {
        val up = Vector3f(0f, 1f, 0f).rotate(camera.rotation)
        val side = Vector3f(to).sub(from).normalize()
        val cross = Vector3f(up).cross(side).normalize()
/*
        val fromToCamera = Vector3f(from).sub(camera.pos.toVector3f()).normalize()
        if (cross.distanceSquared(fromToCamera) > Vector3f(cross).negate().distanceSquared(fromToCamera)) {
            renderParticle(to, from, camera, vertexConsumer)
            return
        }
*/
        val camForward = Vector3f(to).lerp(from, 0.5f)

/*
        val camForward = Vector3f(0f, 0f, -1f).rotateX(camera.pitch * PI.toFloat() / 180).rotateY(camera.yaw * PI.toFloat() / 180).normalize()
        MinecraftClient.getInstance().player?.sendMessage(
            Text.literal("${camera.horizontalPlane.toString(NumberFormat.getInstance())}")
            , true
        )
*/
        //MinecraftClient.getInstance().player?.sendMessage(Text.literal("${camForward.toString(NumberFormat.getInstance())} @ ${camForward.length()}"), true)
        val sparkUp = Vector3f(side).cross(camForward).normalize().mul((sparkWidth / 2).toFloat())
        val light = getBrightness(0f)

        vertexConsumer.vertex(Vector3f(from).add(Vector3f(sparkUp).negate())).light(light).color(Colors.WHITE).texture(maxU, maxV)
        vertexConsumer.vertex(Vector3f(from).add(sparkUp)).light(light).color(Colors.WHITE).texture(maxU, minV)
        vertexConsumer.vertex(Vector3f(to).add(sparkUp)).light(light).color(Colors.WHITE).texture(minU, minV)
        vertexConsumer.vertex(Vector3f(to).add(Vector3f(sparkUp).negate())).light(light).color(Colors.WHITE).texture(minU, maxV)


        vertexConsumer.vertex(Vector3f(to).add(Vector3f(sparkUp).negate())).light(light).color(Colors.WHITE).texture(minU, maxV)
        vertexConsumer.vertex(Vector3f(to).add(sparkUp)).light(light).color(Colors.WHITE).texture(minU, minV)
        vertexConsumer.vertex(Vector3f(from).add(sparkUp)).light(light).color(Colors.WHITE).texture(maxU, minV)
        vertexConsumer.vertex(Vector3f(from).add(Vector3f(sparkUp).negate())).light(light).color(Colors.WHITE).texture(maxU, maxV)
    }

    override fun getBrightness(tint: Float): Int {
        val sparkBrightness: Int = ((age.toFloat() / maxAge) * 15).roundToInt()
        val pos: BlockPos = BlockPos.ofFloored(x, y, z)
        val blockLight: Int = world.getLightLevel(LightType.BLOCK, pos)
        val skyLight: Int = world.getLightLevel(LightType.SKY, pos)
        return LightmapTextureManager.pack(max(sparkBrightness, blockLight), skyLight)
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
                world.random.nextInt(20) + 100,
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
                world.random.nextInt(10) + 50,
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