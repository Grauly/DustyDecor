package grauly.dustydecor.blockentity

import grauly.dustydecor.DustyDecorMod
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
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.min

class VacPipeBlockEntityRenderer(
    private val blockRenderContext: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<VacPipeBlockEntity, BlockEntityRenderState> {
    private val itemModelManager = blockRenderContext.itemModelResolver

    override fun createRenderState(): BlockEntityRenderState = BlockEntityRenderState()

    override fun extractRenderState(
        blockEntity: VacPipeBlockEntity,
        state: BlockEntityRenderState,
        tickProgress: Float,
        cameraPos: Vec3,
        crumblingOverlay: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay)
        val stack = blockEntity.storage.variant.toStack()
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

        state.setData(START_POS, directionToPos(blockEntity.getInsertDirection()))
        state.setData(MIDDLE_POS, directionToPos(null)) //TODO
        state.setData(END_POS, directionToPos(blockEntity.getExtractDirection()))

        if (blockEntity.insertHash != blockEntity.lastInsertHash) {
            blockEntity.ticksSinceLastChange = 0
            blockEntity.lastInsertHash = blockEntity.insertHash
        }

        state.setData(DELTA, blockEntity.ticksSinceLastChange + tickProgress)
    }

    override fun submit(
        state: BlockEntityRenderState,
        matrices: PoseStack,
        queue: SubmitNodeCollector,
        cameraState: CameraRenderState
    ) {
        val itemRenderState: ItemStackRenderState = state.getData(ITEM) ?: return
        val delta: Float = state.getData(DELTA)!!

        var positionOffset: Vec3 = Vec3.ZERO
        val rotation: Quaternionf = Quaternionf()
        val start = state.getData(START_POS)!!
        val middle = state.getData(MIDDLE_POS)!!
        val end = state.getData(END_POS)!!
        val up = Vector3f(0f, -1f, 0f)
        if (false) {
            positionOffset = end
            rotation.rotateTo(up, start.toVector3f())
        } else {
            if (delta <= 0.5) {
                positionOffset = start.lerp(middle, (delta * 2).toDouble())
                rotation.rotateTo(up, start.toVector3f())
            } else if (delta >= 0.5) {
                positionOffset = middle.lerp(end, min((delta - 0.5) * 2, 2.0))
                rotation.rotateTo(up, (if (middle == end) start else end).reverse().toVector3f())
            }
        }

        matrices.pushPose()
        matrices.translate(0.5, 0.5, 0.5)
        matrices.translate(positionOffset)
        matrices.mulPose(rotation)

        itemRenderState.submit(
            matrices,
            queue,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            0
        )

        matrices.popPose()
    }

    private fun directionToPos(direction: Direction?): Vec3 =
        when (direction) {
            Direction.UP -> Vec3(0.0, 0.5, 0.0)
            Direction.DOWN -> Vec3(0.0, -0.5, 0.0)
            Direction.NORTH -> Vec3(0.0, 0.0, -0.5)
            Direction.SOUTH -> Vec3(0.0, 0.0, 0.5)
            Direction.EAST -> Vec3(0.5, 0.0, 0.0)
            Direction.WEST -> Vec3(-0.5, 0.0, 0.0)
            else -> Vec3.ZERO
        }

    companion object {
        val ITEM: RenderStateDataKey<ItemStackRenderState> = RenderStateDataKey<ItemStackRenderState>.create()
        val NEEDS_ITEM_MOVEMENT: RenderStateDataKey<Boolean> = RenderStateDataKey<Boolean>.create()
        val DELTA: RenderStateDataKey<Float> = RenderStateDataKey<Float>.create()
        val START_POS: RenderStateDataKey<Vec3> = RenderStateDataKey<Vec3>.create()
        val MIDDLE_POS: RenderStateDataKey<Vec3> = RenderStateDataKey<Vec3>.create()
        val END_POS: RenderStateDataKey<Vec3> = RenderStateDataKey<Vec3>.create()
    }
}