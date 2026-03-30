package grauly.dustydecor.blockentity

import com.mojang.blaze3d.vertex.PoseStack
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.furniture.PhoneBlock
import it.unimi.dsi.fastutil.HashCommon
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import kotlin.math.PI
import kotlin.math.sin

class PhoneRenderer(
    private val blockRenderContext: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<PhoneBlockEntity, BlockEntityRenderState> {
    override fun createRenderState(): BlockEntityRenderState = BlockEntityRenderState()

    override fun extractRenderState(
        blockEntity: PhoneBlockEntity,
        state: BlockEntityRenderState,
        partialTicks: Float,
        cameraPosition: Vec3,
        breakProgress: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        if (HANDSET_STACK == null) {
            HANDSET_STACK = ModItems.PHONE_HANDSET.defaultInstance
        }
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress)
        val ringing = blockEntity.blockState.getValue(PhoneBlock.RINGING)
        if (!ringing) return

        val itemRenderState = ItemStackRenderState()
        blockRenderContext.itemModelResolver.updateForTopItem(
            itemRenderState,
            HANDSET_STACK!!,
            ItemDisplayContext.NONE,
            blockEntity.level,
            blockEntity,
            HashCommon.long2int(blockEntity.blockPos.asLong())
        )
        state.setData(HANDSET_STATE, itemRenderState)
        state.setData(Y_ROTATION, Math.toRadians(blockEntity.visualRotationYInDegrees.toDouble()).toFloat())
        state.setData(DELTA, ((blockEntity.ringTicks % blockEntity.ringCycle) + partialTicks) / blockEntity.ringCycle)
        if (blockEntity.ringTicks < 0) state.setData(DELTA, 0f)
    }

    override fun submit(
        state: BlockEntityRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        val handsetRenderState = state.getData(HANDSET_STATE) ?: return
        val mainRotation = Quaternionf().rotateY(state.getData(Y_ROTATION)!!)
        val ringRotation = Quaternionf().rotateZ((sin(state.getData(DELTA)!! * 2*PI)).toFloat() * 0.01f * PI.toFloat())
        poseStack.pushPose()
        poseStack.translate(0.5, 0.5, 0.5)
        poseStack.mulPose(mainRotation)
        poseStack.translate(0.0, 4.0/16, 2.5/16)
        poseStack.mulPose(ringRotation)
        handsetRenderState.submit(
            poseStack,
            submitNodeCollector,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            0
        )
        poseStack.popPose()
    }

    companion object {
        val HANDSET_STATE: RenderStateDataKey<ItemStackRenderState> = RenderStateDataKey<ItemStackRenderState>.create()
        var HANDSET_STACK: ItemStack? = null
        val Y_ROTATION: RenderStateDataKey<Float> = RenderStateDataKey<Float>.create()
        val DELTA: RenderStateDataKey<Float> = RenderStateDataKey<Float>.create()
    }
}