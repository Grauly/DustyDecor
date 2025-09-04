package grauly.dustydecor.blockentity

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModParticleTypes
import grauly.dustydecor.geometry.AlertBeamsShape
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.command.ModelCommandRenderer
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.pow

class TallCageLampBlockEntityRenderer(
    private val blockRenderContext: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<TallCageLampBlockEntity, TallCageLampBlockEntityRenderingContext> {


    //TODO: update with FAPI ASAP
    override fun createRenderState(): TallCageLampBlockEntityRenderingContext {
        return TallCageLampBlockEntityRenderingContext(false, 0f, Vec3d.ZERO, Vec3d.ZERO, -1)
    }

    override fun updateRenderState(
        blockEntity: TallCageLampBlockEntity,
        state: TallCageLampBlockEntityRenderingContext,
        tickProgress: Float,
        cameraPos: Vec3d,
        crumblingOverlay: ModelCommandRenderer.CrumblingOverlayCommand?
    ) {
        super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay)
        state.time = blockEntity.age + tickProgress
        state.shouldShowBeams = blockEntity.shouldShowBeams()
        state.rotationAxis = blockEntity.getRotationDirection()
        state.color = blockEntity.color
        state.cameraPos = cameraPos
    }


    override fun rendersOutsideBoundingBox(): Boolean = true

    override fun render(
        state: TallCageLampBlockEntityRenderingContext,
        matrices: MatrixStack,
        orderedRenderCommandQueue: OrderedRenderCommandQueue
    ) {
        if (!state.shouldShowBeams) return
        val offsetWorldPos = state.pos.toCenterPos().add(state.rotationAxis.multiply(-3 / 16.0))
        val camRelativeOffset = offsetWorldPos.subtract(state.cameraPos)
        val rotation = Quaternionf()
            .rotateTo(Vector3f(0f, 1f, 0f), state.rotationAxis.toVector3f())
            .mul(Quaternionf().rotateY((state.time * ROTATION_PER_TICK).toFloat()))

        //TODO: find a better way to do this
        doLightBlinding(rotation, camRelativeOffset, offsetWorldPos)

        matrices.push()
        matrices.translate(0.5, 0.5, 0.5)
        matrices.translate(state.rotationAxis.multiply(-3/16.0))
        matrices.multiply(rotation)
        orderedRenderCommandQueue.submitCustom(
            matrices,
            renderLayer
        ) { matrixStack, vertexConsumer ->
            AlertBeamsShape
                .applyMatrix(matrixStack, vertexConsumer, Vec2f(0f, 0f), Vec2f(1f, 1f)) {
                    it.color(state.color).light(state.lightmapCoordinates).normal(0f, 1f, 0f).overlay(OverlayTexture.DEFAULT_UV)
                }
        }
        matrices.pop()

    }

    private fun doLightBlinding(
        rotation: Quaternionf?,
        camRelativeOffset: Vec3d,
        offsetWorldPos: Vec3d?
    ) {
        val blindingTestVector = Vector3f(1f, 0f, 0f).rotate(rotation)
        if (camRelativeOffset.lengthSquared() < MAX_BLINDING_DISTANCE.pow(2)) {
            if (
                camRelativeOffset.normalize()
                    .squaredDistanceTo(Vec3d(blindingTestVector)) < BLINDING_THRESHOLD.pow(2) ||
                camRelativeOffset.normalize()
                    .squaredDistanceTo(Vec3d(blindingTestVector.negate())) < BLINDING_THRESHOLD.pow(2)
            ) {
                val spawnPoint = camRelativeOffset.negate().normalize().multiply(5 / 16.0).add(offsetWorldPos)
                blockRenderContext.renderDispatcher.world.addParticleClient(
                    ModParticleTypes.LIGHT_FLASH,
                    spawnPoint.x,
                    spawnPoint.y,
                    spawnPoint.z,
                    0.0,
                    0.0,
                    0.0
                )
            }
        }
    }

    companion object {
        private const val ROTATION_PER_TICK: Double = PI / 20
        private const val BLINDING_THRESHOLD: Double = 0.02
        private const val MAX_BLINDING_DISTANCE: Double = 6.0
        private val renderLayer = RenderLayer.getBeaconBeam(Identifier.of(DustyDecorMod.MODID, "textures/block/cage_lamp_beam.png"), true)
    }

}