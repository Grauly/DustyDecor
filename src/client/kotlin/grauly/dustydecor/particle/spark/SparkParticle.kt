package grauly.dustydecor.particle.spark

import grauly.dustydecor.ModParticleTypes
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.Camera
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.CommonColors
import net.minecraft.world.phys.HitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.LightCoordsUtil
import net.minecraft.world.phys.Vec3
import net.minecraft.util.RandomSource
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.ClipContext
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
    clientWorld: ClientLevel,
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
    private val spriteProvider: SpriteSet
) : Particle(clientWorld, x, y, z, velocityX, velocityY, velocityZ) {
    private var pos: Vec3 = Vec3(x, y, z)
    private var lastPos: Vec3 = pos
    private var lastLastPos: Vec3 = lastPos
    private var velocity: Vec3 = Vec3(velocityX, velocityY, velocityZ)

    private val bounceFactor = 0.6
    private val sparkWidth: Double = sparkWidthPixels / 16
    private var hasSplit = false
    private var lastBouncedBlockPos = BlockPos.ZERO
    private var scale = 1f
    private var sprite: TextureAtlasSprite = spriteProvider.get(0, lifetime)

    init {
        this.gravity = gravity.toFloat()
        this.friction = drag.toFloat()
        this.lifetime = lifetime
        scale = 0.9f + random.nextFloat() * 0.2f
    }

    override fun tick() {
        if (this.removed) return
        if (age++ > lifetime) {
            this.remove()
            return
        }
        sprite = spriteProvider.get(age, lifetime)
        lastLastPos = lastPos
        lastPos = pos
        velocity = velocity.scale(friction.toDouble()).add(0.0, -0.04 * gravity, 0.0)
        var prospectivePos = pos.add(velocity)
        val hit = level.clip(
            ClipContext(
                pos,
                prospectivePos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                CollisionContext.empty()
            )
        )
        if (hit.type != HitResult.Type.MISS) {
            if (lastBouncedBlockPos == hit.blockPos) {
                //bounced again too fast in same block, presumed in block
                this.remove()
                return
            }
            lastBouncedBlockPos = hit.blockPos
            val multVector = when (hit.direction.axis) {
                Direction.Axis.X -> Vec3(-1.0, 1.0, 1.0)
                Direction.Axis.Y -> Vec3(1.0, -1.0, 1.0)
                Direction.Axis.Z -> Vec3(1.0, 1.0, -1.0)
                null -> Vec3(1.0, 1.0, 1.0)
            }
            val distanceToBounce = hit.location.subtract(pos).length()
            velocity = velocity.multiply(multVector).scale(bounceFactor)
            val speed = velocity.length()
            val afterBounceLength = speed - distanceToBounce
            prospectivePos = hit.location.add(velocity.normalize().scale(afterBounceLength))
            onBounce()
        } else {
            lastBouncedBlockPos = BlockPos.ZERO
        }
        pos = prospectivePos
    }

    override fun getGroup(): ParticleRenderType = SparkParticleRenderer.textureSheet

    private fun onBounce() {
        val randomNum = random.nextInt(10)
        if (randomNum < 2) {
            level.addParticle(
                ModParticleTypes.SPARK_FLASH,
                pos.x,
                pos.y,
                pos.z,
                velocity.x,
                velocity.y,
                velocity.z
            )
        }
        if (randomNum < 1) {
            split()
        }
    }

    private fun split() {
        level.addParticle(ModParticleTypes.SPARK_FLASH, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z)
        if (!hasSplit) {
            val velocitySpread = velocity.length() * 0.6
            hasSplit = true
            level.addParticle(
                ModParticleTypes.SMALL_SPARK_PARTICLE,
                pos.x,
                pos.y,
                pos.z,
                velocity.x * 0.6f.pow(2) + random.nextFloat() * velocitySpread * 2 - velocitySpread,
                velocity.y * 0.6f.pow(2) + random.nextFloat() * velocitySpread * 2 - velocitySpread,
                velocity.z * 0.6f.pow(2) + random.nextFloat() * velocitySpread * 2 - velocitySpread,
            )
        }
    }

    fun intersectsFrustum(frustum: Frustum): Boolean {
        val posIntersects = frustum.pointInFrustum(pos.x, pos.y, pos.z)
        val lastPosIntersects = frustum.pointInFrustum(lastLastPos.x, lastLastPos.y, lastLastPos.z)
        return posIntersects || lastPosIntersects
    }

    fun render(
        submittable: QuadBasedParticleSubmittable,
        camera: Camera,
        tickProgress: Float
    ) {
        val renderLocalPos = lastPos.lerp(pos, tickProgress.toDouble()).subtract(camera.position())
        val renderLocalLastPos = lastLastPos.lerp(lastPos, tickProgress.toDouble())
            .subtract(camera.position())
        val centerPos = renderLocalLastPos.lerp(renderLocalPos, 0.5)
        val spanVector = renderLocalPos.subtract(renderLocalLastPos)
        val speedSquared = spanVector.lengthSqr()
        val scaleVector = Vector3f(
            max(lengthFactor * speedSquared.toFloat(), getSize(tickProgress)),
            getSize(tickProgress),
            getSize(tickProgress)
        )
        val rotation = if (spanVector.lengthSqr() != 0.0) {
            Quaternionf().rotationTo(Vector3f(1f, 0f, 0f), spanVector.normalize().toVector3f())
        } else {
            Quaternionf()
        }
        val baseVector = Vector3f(scaleVector.x, 0f, 0f).rotate(rotation).mul(0.5f)
        renderParticle(
            centerPos.toVector3f().add(baseVector),
            centerPos.toVector3f().add(baseVector.negate()),
            submittable,
            tickProgress
        )
    }

    private fun renderParticle(
        from: Vector3f,
        to: Vector3f,
        submittable: QuadBasedParticleSubmittable,
        tickProgress: Float
    ) {
        val side = Vector3f(to).sub(from).normalize()
        val camForward = Vector3f(to).lerp(from, 0.5f)
        val sparkUp = Vector3f(side).cross(camForward).normalize().mul((getSize(tickProgress) / 2))
        val light = getLightCoords(0f)

        submittable.beginQuad()
        submittable.addVertex(
            Vector3f(to).add(Vector3f(sparkUp).negate()),
            sprite.u0,
            sprite.v1,
            light,
            CommonColors.WHITE
        )
        submittable.addVertex(
            Vector3f(to).add(sparkUp),
            sprite.u0,
            sprite.v0,
            light,
            CommonColors.WHITE
        )
        submittable.addVertex(
            Vector3f(from).add(sparkUp),
            sprite.u1,
            sprite.v0,
            light,
            CommonColors.WHITE
        )
        submittable.addVertex(
            Vector3f(from).add(Vector3f(sparkUp).negate()),
            sprite.u1,
            sprite.v1,
            light,
            CommonColors.WHITE
        )
        submittable.endQuad()
    }

    override fun getLightCoords(tint: Float): Int {
        val sparkBrightness: Int = ((age.toFloat() / lifetime) * 15).roundToInt()
        val pos: BlockPos = BlockPos.containing(pos)
        val blockLight: Int = level.getBrightness(LightLayer.BLOCK, pos)
        val skyLight: Int = level.getBrightness(LightLayer.SKY, pos)
        return LightCoordsUtil.pack(max(sparkBrightness, blockLight), skyLight)
    }

    private fun getSize(tickProgress: Float): Float {
        return sparkWidth.toFloat() * scale * (1f - (age + tickProgress) / lifetime)
    }

    private fun pixel(i: Int): Double = i / 16.0
    private fun pixel(i: Double): Double = i / 16.0
    private fun pixel(i: Float): Float = i / 16.0f

    class LargeSparkFactory(private val spriteProvider: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            parameters: SimpleParticleType,
            world: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle? {
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

    class SmallSparkFactory(private val spriteProvider: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            parameters: SimpleParticleType,
            world: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle? {
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
        private fun randomDoubleBetween(random: RandomSource, start: Double, end: Double): Double {
            val base = random.nextDouble()
            val diff = end - start
            return start + (base * diff)
        }
    }
}