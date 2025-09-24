package grauly.dustydecor.blockentity

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.geometry.AlertBeamsShape
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState
import net.minecraft.client.render.command.ModelCommandRenderer
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.state.CameraRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

class TallCageLampBlockEntityRenderer(
    private val blockRenderContext: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<TallCageLampBlockEntity, BlockEntityRenderState> {


    override fun createRenderState(): BlockEntityRenderState {
        return BlockEntityRenderState()
    }

    override fun updateRenderState(
        blockEntity: TallCageLampBlockEntity,
        state: BlockEntityRenderState,
        tickProgress: Float,
        cameraPos: Vec3d,
        crumblingOverlay: ModelCommandRenderer.CrumblingOverlayCommand?
    ) {
        super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay)
        state.setData(TIME, blockEntity.age + tickProgress)
        state.setData(SHOULD_SHOW_BEAMS, blockEntity.shouldShowBeams())
        state.setData(ROTATION_AXIS, blockEntity.getRotationDirection())
        state.setData(COLOR, blockEntity.color)
    }

    override fun render(
        state: BlockEntityRenderState,
        matrices: MatrixStack,
        queue: OrderedRenderCommandQueue,
        cameraState: CameraRenderState
    ) {
        if (state.getData(SHOULD_SHOW_BEAMS) == false) return
        val rotationAxis: Vec3d = state.getData(ROTATION_AXIS)!!
        val rotation = Quaternionf()
            .rotateTo(Vector3f(0f, 1f, 0f), rotationAxis.toVector3f())
            .mul(Quaternionf().rotateY((state.getData(TIME)!! * ROTATION_PER_TICK).toFloat()))

        matrices.push()
        matrices.translate(0.5, 0.5, 0.5)
        matrices.translate(rotationAxis.multiply(-3 / 16.0))
        matrices.multiply(rotation)
        queue.submitCustom(
            matrices,
            renderLayer
        ) { matrixStack, vertexConsumer ->
            AlertBeamsShape
                .applyMatrix(matrixStack, vertexConsumer, Vec2f(0f, 0f), Vec2f(1f, 1f)) {
                    it.color(state.getData(COLOR)!!).light(state.lightmapCoordinates).normal(0f, 1f, 0f)
                        .overlay(OverlayTexture.DEFAULT_UV)
                }
        }
        matrices.pop()
    }

    override fun rendersOutsideBoundingBox(): Boolean = true

    companion object {
        private const val ROTATION_PER_TICK: Double = PI / 20

        private val TIME: RenderStateDataKey<Float> = RenderStateDataKey.create<Float>()
        private val SHOULD_SHOW_BEAMS: RenderStateDataKey<Boolean> = RenderStateDataKey.create<Boolean>()
        private val ROTATION_AXIS: RenderStateDataKey<Vec3d> = RenderStateDataKey.create<Vec3d>()
        private val COLOR: RenderStateDataKey<Int> = RenderStateDataKey.create<Int>()
        private val renderLayer =
            RenderLayer.getBeaconBeam(Identifier.of(DustyDecorMod.MODID, "textures/block/cage_lamp_beam.png"), true)
    }

}