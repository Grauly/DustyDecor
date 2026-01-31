package grauly.dustydecor.blockentity

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity
import it.unimi.dsi.fastutil.HashCommon
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.state.CameraRenderState
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.phys.Vec3

class VacPipeStationBlockEntityRenderer(
    private val blockRenderContext: BlockEntityRendererProvider.Context
): BlockEntityRenderer<VacPipeStationBlockEntity, BlockEntityRenderState> {
    private val itemModelManager = blockRenderContext.itemModelResolver

    override fun createRenderState(): BlockEntityRenderState = BlockEntityRenderState()

    override fun extractRenderState(
        blockEntity: VacPipeStationBlockEntity,
        state: BlockEntityRenderState,
        tickProgress: Float,
        cameraPos: Vec3,
        crumblingOverlay: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay)
        val stack = blockEntity.storage.getSlot(0).resource.toStack()
        if (stack.isEmpty) return

        val itemRenderState = ItemStackRenderState()
        itemModelManager.updateForTopItem(
            itemRenderState,
            stack,
            ItemDisplayContext.NONE,
            blockEntity.level,
            blockEntity,
            HashCommon.long2int(blockEntity.blockPos.asLong())
        )
        state.setData(ITEM, itemRenderState)
    }

    override fun submit(
        state: BlockEntityRenderState,
        matrices: PoseStack,
        queue: SubmitNodeCollector,
        cameraState: CameraRenderState
    ) {
        val itemRenderState = state.getData(ITEM) ?: return
        val DISPLAY_CENTER = Vec3(.5, 6/16.0, .5)
        matrices.pushPose()
        matrices.translate(DISPLAY_CENTER)

        itemRenderState.submit(
            matrices,
            queue,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            0
        )

        matrices.popPose()
    }

    companion object {
        val ITEM: RenderStateDataKey<ItemStackRenderState> = RenderStateDataKey<ItemStackRenderState>.create()
    }
}