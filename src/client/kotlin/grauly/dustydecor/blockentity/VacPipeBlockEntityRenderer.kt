package grauly.dustydecor.blockentity

import net.minecraft.client.item.ItemModelManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.command.ModelCommandRenderer
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.entity.ItemEntityRenderer
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState
import net.minecraft.client.render.item.ItemRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemDisplayContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random

class VacPipeBlockEntityRenderer(
    private val blockRenderContext: BlockEntityRendererFactory.Context,
    private val itemModelManager: ItemModelManager = blockRenderContext.itemModelManager
): BlockEntityRenderer<VacPipeBlockEntity, VacPipeBlockEntityRendererContext> {
    override fun createRenderState(): VacPipeBlockEntityRendererContext {
        return VacPipeBlockEntityRendererContext()
    }

    override fun updateRenderState(
        blockEntity: VacPipeBlockEntity,
        state: VacPipeBlockEntityRendererContext,
        tickProgress: Float,
        cameraPos: Vec3d,
        crumblingOverlay: ModelCommandRenderer.CrumblingOverlayCommand?
    ) {
        super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay)
        val stack = blockEntity.storage.variant.toStack()
        if (!stack.isEmpty) {
            val itemRenderState: ItemRenderState = ItemRenderState()
            val seed = BlockPos.asLong(blockEntity.pos.x, blockEntity.pos.y, blockEntity.pos.z)
            itemModelManager.clearAndUpdate(
                itemRenderState,
                stack,
                ItemDisplayContext.HEAD,
                blockEntity.world,
                blockEntity,
                seed.toInt()
            )
        }
    }

    override fun render(
        state: VacPipeBlockEntityRendererContext,
        matrices: MatrixStack,
        queue: OrderedRenderCommandQueue
    ) {
        matrices.push()
        state.itemRenderState.render(matrices, queue, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0)
        matrices.pop()
    }

    companion object {
        val RANDOM = Random.create()
    }
}