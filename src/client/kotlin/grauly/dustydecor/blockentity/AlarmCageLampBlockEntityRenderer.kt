package grauly.dustydecor.blockentity

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.geometry.AlertBeamsShape
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.state.CameraRenderState
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.resources.Identifier
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

class AlarmCageLampBlockEntityRenderer(
    private val blockRenderContext: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<AlarmCageLampBlockEntity, BlockEntityRenderState> {


    override fun createRenderState(): BlockEntityRenderState {
        return BlockEntityRenderState()
    }

    override fun extractRenderState(
        blockEntity: AlarmCageLampBlockEntity,
        state: BlockEntityRenderState,
        tickProgress: Float,
        cameraPos: Vec3,
        crumblingOverlay: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay)
        state.setData(TIME, blockEntity.age + tickProgress)
        state.setData(SHOULD_SHOW_BEAMS, blockEntity.shouldShowBeams())
        state.setData(ROTATION_AXIS, blockEntity.getRotationDirection())
        state.setData(COLOR, blockEntity.color)
    }

    override fun submit(
        state: BlockEntityRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        cameraState: CameraRenderState
    ) {
        if (state.getData(SHOULD_SHOW_BEAMS) == false) return
        val rotationAxis: Vec3 = state.getData(ROTATION_AXIS)!!
        val rotation = Quaternionf()
            .rotateTo(Vector3f(0f, 1f, 0f), rotationAxis.toVector3f())
            .mul(Quaternionf().rotateY((state.getData(TIME)!! * ROTATION_PER_TICK).toFloat()))

        poseStack.pushPose()
        poseStack.translate(0.5, 0.5, 0.5)
        poseStack.translate(rotationAxis.scale(-3 / 16.0))
        poseStack.mulPose(rotation)
        collector.submitCustomGeometry(
            poseStack,
            renderType
        ) { matrixStack, vertexConsumer ->
            AlertBeamsShape
                .applyMatrix(matrixStack, vertexConsumer, Vec2(0f, 0f), Vec2(1f, 1f)) {
                    it.setColor(state.getData(COLOR)!!).setLight(state.lightCoords).setNormal(0f, 1f, 0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                }
        }
        poseStack.popPose()
    }

    override fun shouldRenderOffScreen(): Boolean = true

    companion object {
        private const val ROTATION_PER_TICK: Double = PI / 20

        private val TIME: RenderStateDataKey<Float> = RenderStateDataKey.create<Float>()
        private val SHOULD_SHOW_BEAMS: RenderStateDataKey<Boolean> = RenderStateDataKey.create<Boolean>()
        private val ROTATION_AXIS: RenderStateDataKey<Vec3> = RenderStateDataKey.create<Vec3>()
        private val COLOR: RenderStateDataKey<Int> = RenderStateDataKey.create<Int>()
        private val renderType =
            RenderTypes.beaconBeam(Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "textures/block/cage_lamp_beam.png"), true)
    }

}