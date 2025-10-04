package grauly.dustydecor.blockentity

import grauly.dustydecor.DustyDecorMod
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
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.min

class VacPipeBlockEntityRenderer(
    private val blockRenderContext: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<VacPipeBlockEntity, BlockEntityRenderState> {
    private val itemModelManager = blockRenderContext.itemModelManager

    override fun createRenderState(): BlockEntityRenderState? = BlockEntityRenderState()

    override fun updateRenderState(
        blockEntity: VacPipeBlockEntity,
        state: BlockEntityRenderState,
        tickProgress: Float,
        cameraPos: Vec3d,
        crumblingOverlay: ModelCommandRenderer.CrumblingOverlayCommand?
    ) {
        super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay)
        val stack = blockEntity.storage.variant.toStack()
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

        state.setData(START_POS, directionToPos(blockEntity.getInsertDirection()))
        state.setData(MIDDLE_POS, directionToPos(null)) //TODO
        state.setData(END_POS, directionToPos(blockEntity.getExtractDirection()))

        if (blockEntity.insertHash != blockEntity.lastInsertHash) {
            blockEntity.ticksSinceLastChange = 0
            blockEntity.lastInsertHash = blockEntity.insertHash
        }

        state.setData(DELTA, blockEntity.ticksSinceLastChange + tickProgress)
    }

    override fun render(
        state: BlockEntityRenderState,
        matrices: MatrixStack,
        queue: OrderedRenderCommandQueue,
        cameraState: CameraRenderState
    ) {
        val itemRenderState: ItemRenderState = state.getData(ITEM) ?: return
        val delta: Float = state.getData(DELTA)!!

        var positionOffset: Vec3d = Vec3d.ZERO
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
                rotation.rotateTo(up, (if (middle == end) start else end).negate().toVector3f())
            }
        }

        matrices.push()
        matrices.translate(0.5, 0.5, 0.5)
        matrices.translate(positionOffset)
        matrices.multiply(rotation)

        itemRenderState.render(
            matrices,
            queue,
            state.lightmapCoordinates,
            OverlayTexture.DEFAULT_UV,
            0
        )

        matrices.pop()
    }

    private fun directionToPos(direction: Direction?): Vec3d =
        when (direction) {
            Direction.UP -> Vec3d(0.0, 0.5, 0.0)
            Direction.DOWN -> Vec3d(0.0, -0.5, 0.0)
            Direction.NORTH -> Vec3d(0.0, 0.0, -0.5)
            Direction.SOUTH -> Vec3d(0.0, 0.0, 0.5)
            Direction.EAST -> Vec3d(0.5, 0.0, 0.0)
            Direction.WEST -> Vec3d(-0.5, 0.0, 0.0)
            else -> Vec3d.ZERO
        }

    companion object {
        val ITEM: RenderStateDataKey<ItemRenderState> = RenderStateDataKey<ItemRenderState>.create()
        val NEEDS_ITEM_MOVEMENT: RenderStateDataKey<Boolean> = RenderStateDataKey<Boolean>.create()
        val DELTA: RenderStateDataKey<Float> = RenderStateDataKey<Float>.create()
        val START_POS: RenderStateDataKey<Vec3d> = RenderStateDataKey<Vec3d>.create()
        val MIDDLE_POS: RenderStateDataKey<Vec3d> = RenderStateDataKey<Vec3d>.create()
        val END_POS: RenderStateDataKey<Vec3d> = RenderStateDataKey<Vec3d>.create()
    }
}