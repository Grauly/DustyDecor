package grauly.dustydecor.particle

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.state.level.QuadParticleRenderState
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.floor

class PhoneRingParticle(
    private val attached: Boolean,
    private val flipped: Boolean,
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    sprites: SpriteSet,
    gravity: Float
) : FixedRotationOffsetParticle(
    level,
    if (attached) floor(x) + 0.5f else x,
    if (attached) floor(y) else y,
    if (attached) floor(z) + 0.5f else z,
    sprites,
    gravity
) {
    init {
        lifetime = 16
        quadSize = 0.15f
    }

    private val axisRotationRadians: Float = level.random.nextFloat() * 2 * PI.toFloat()

    private val baseOffset = Vec3(quadSize.toDouble(), quadSize.toDouble(), 0.0)
    private val baseRotation = Quaternionf().rotateY(axisRotationRadians)
    private val attachedRotation = if (!attached) baseRotation else {
        val pos = BlockPos.containing(x, y, z)
        val state = level.getBlockState(pos)
        if (!state.`is`(ModBlocks.PHONE)) baseRotation else {
            val rotation = state.getValue(SingleFurnitureBlock.ROTATION)
            Quaternionf().rotateY(-2 * PI.toFloat() * rotation / 16.0f)
        }
    }

    override fun getOffset(): Vector3f = baseOffset.toVector3f()
    override fun getRotation(): Quaternionf = baseRotation

    override fun extractRotatedQuad(
        renderState: QuadParticleRenderState,
        camera: Camera,
        rotation: Quaternionf,
        tickProgress: Float
    ) {

        if (!attached) {
            super.extractRotatedQuad(renderState, camera, rotation, tickProgress)
            return
        }

        val offset = (if (flipped) LEFT_HANDSET_OFFSET else RIGHT_HANDSET_OFFSET)
            .add(baseOffset.multiply((if (flipped) 1.0 else -1.0), 1.0, 1.0))
            .toVector3f()
            .rotate(attachedRotation)
        val cameraPos = camera.position()
        val x1: Float = (x + offset.x - cameraPos.x).toFloat()
        val y1: Float = (y + offset.y - cameraPos.y).toFloat()
        val z1: Float = (z + offset.z - cameraPos.z).toFloat()
        this.extractRotatedQuad(
            renderState,
            Quaternionf(attachedRotation).rotateY(if (!flipped) PI.toFloat() else 0f),
            x1,
            y1,
            z1,
            tickProgress
        )
    }

    class Provider(private val sprites: SpriteSet) : ParticleProvider<PhoneRingParticleOptions> {
        override fun createParticle(
            options: PhoneRingParticleOptions,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xAux: Double,
            yAux: Double,
            zAux: Double,
            random: RandomSource
        ): Particle {
            return PhoneRingParticle(
                options.attached,
                options.flipped,
                level,
                x, y, z,
                sprites,
                0.0f,
            )
        }
    }

    companion object {
        val LEFT_HANDSET_OFFSET = Vec3(5.5, 6.0, 2.5).scale(1 / 16.0)
        val RIGHT_HANDSET_OFFSET = LEFT_HANDSET_OFFSET.multiply(-1.0, 1.0, 1.0)
    }
}