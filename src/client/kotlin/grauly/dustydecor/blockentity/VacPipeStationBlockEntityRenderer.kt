package grauly.dustydecor.blockentity

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity
import it.unimi.dsi.fastutil.HashCommon
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState
import net.minecraft.client.render.command.ModelCommandRenderer
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.item.ItemRenderState
import net.minecraft.client.render.state.CameraRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemDisplayContext
import net.minecraft.util.math.Vec3d

class VacPipeStationBlockEntityRenderer(
    private val blockRenderContext: BlockEntityRendererFactory.Context
): BlockEntityRenderer<VacPipeStationBlockEntity, BlockEntityRenderState> {
    private val itemModelManager = blockRenderContext.itemModelManager

    override fun createRenderState(): BlockEntityRenderState = BlockEntityRenderState()

    override fun updateRenderState(
        blockEntity: VacPipeStationBlockEntity,
        state: BlockEntityRenderState,
        tickProgress: Float,
        cameraPos: Vec3d,
        crumblingOverlay: ModelCommandRenderer.CrumblingOverlayCommand?
    ) {
        super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay)
        val stack = blockEntity.storage.getSlot(0).resource.toStack()
        if (stack.isEmpty) return

        val itemRenderState = ItemRenderState()
        itemModelManager.clearAndUpdate(
            itemRenderState,
            stack,
            ItemDisplayContext.NONE,
            blockEntity.world,
            blockEntity,
            HashCommon.long2int(blockEntity.pos.asLong())
        )
        state.setData(ITEM, itemRenderState)
    }

    override fun render(
        state: BlockEntityRenderState,
        matrices: MatrixStack,
        queue: OrderedRenderCommandQueue,
        cameraState: CameraRenderState
    ) {
        val itemRenderState = state.getData(ITEM) ?: return
        val DISPLAY_CENTER = Vec3d(.5, 6/16.0, .5)
        matrices.push()
        matrices.translate(DISPLAY_CENTER)

        itemRenderState.render(
            matrices,
            queue,
            state.lightmapCoordinates,
            OverlayTexture.DEFAULT_UV,
            0
        )

        matrices.pop()
    }

    companion object {
        val ITEM: RenderStateDataKey<ItemRenderState> = RenderStateDataKey<ItemRenderState>.create()
    }
}