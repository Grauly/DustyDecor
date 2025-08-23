package grauly.dustydecor.particle

import grauly.dustydecor.ModParticleTypes
import net.minecraft.block.ShapeContext
import net.minecraft.client.particle.*
import net.minecraft.client.render.Camera
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.Colors
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.util.math.random.Random
import net.minecraft.world.LightType
import net.minecraft.world.RaycastContext
import org.joml.Quaternionf
import org.joml.Vector3f
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
    private var pos: Vec3d = Vec3d(x,y,z)
    private var lastPos: Vec3d = pos
    private var lastLastPos: Vec3d = lastPos
    private var velocity: Vec3d = Vec3d(velocityX,velocityY,velocityZ)

    private val bounceFactor = 0.6
    private val sparkWidth: Double = sparkWidthPixels / 16
    private var hasSplit = false
    private var lastBouncedBlockPos = BlockPos.ZERO

    init {
        this.gravityStrength = gravity.toFloat()
        this.velocityMultiplier = drag.toFloat()
        maxAge = lifetime
        setSprite(spriteProvider.getSprite(age, maxAge))
        scale = 0.9f + random.nextFloat() * 0.2f
    }

    override fun tick() {
        if (this.dead) return
        if (age++ > maxAge) {
            this.markDead()
            return
        }
        lastLastPos = lastPos
        lastPos = pos
        velocity = velocity.multiply(velocityMultiplier.toDouble()).add(0.0, -0.04 * gravityStrength, 0.0)
        var prospectivePos = pos.add(velocity)
        val hit = world.raycast(RaycastContext(
            pos, prospectivePos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent()
        ))
        if (hit.type != HitResult.Type.MISS) {
            if (lastBouncedBlockPos == hit.blockPos) {
                //bounced again too fast in same block, presumed in block
                this.markDead()
                return
            }
            lastBouncedBlockPos = hit.blockPos
            val multVector = when (hit.side.axis) {
                Direction.Axis.X -> Vec3d(-1.0, 1.0, 1.0)
                Direction.Axis.Y -> Vec3d(1.0, -1.0, 1.0)
                Direction.Axis.Z -> Vec3d(1.0, 1.0, -1.0)
                null -> Vec3d(1.0,1.0,1.0)
            }
            val distanceToBounce = hit.pos.subtract(pos).length()
            velocity = velocity.multiply(multVector).multiply(bounceFactor)
            val speed = velocity.length()
            val afterBounceLength = speed - distanceToBounce
            prospectivePos = hit.pos.add(velocity.normalize().multiply(afterBounceLength))
            onBounce()
        } else {
            lastBouncedBlockPos = BlockPos.ZERO
        }
        pos = prospectivePos
        setSprite(spriteProvider.getSprite(age, maxAge))
    }

    private fun onBounce() {
        val randomNum = random.nextInt(10)
        if (randomNum < 2) {
            world.addParticleClient(ModParticleTypes.SPARK_FLASH, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z)
        }
        if (randomNum < 1) {
            split()
        }
    }

    private fun split() {
        world.addParticleClient(ModParticleTypes.SPARK_FLASH, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z)
        if (!hasSplit) {
            val velocitySpread = velocity.length() * 0.6
            hasSplit = true
            world.addParticleClient(
                ModParticleTypes.SMALL_SPARK_PARTICLE_TYPE,
                pos.x,
                pos.y,
                pos.z,
                velocity.x * 0.6f.pow(2) + random.nextFloat() * velocitySpread * 2 - velocitySpread,
                velocity.y * 0.6f.pow(2) + random.nextFloat() * velocitySpread * 2 - velocitySpread,
                velocity.z * 0.6f.pow(2) + random.nextFloat() * velocitySpread * 2 - velocitySpread,
            )
        }
    }

    override fun render(
        vertexConsumer: VertexConsumer,
        camera: Camera,
        quaternionf: Quaternionf,
        tickProgress: Float
    ) {
        val renderLocalPos =
            lastPos.lerp(pos, tickProgress.toDouble()).subtract(camera.pos)
        val renderLocalLastPos =
            lastLastPos.lerp(lastPos, tickProgress.toDouble())
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
        val pos: BlockPos = BlockPos.ofFloored(pos)
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