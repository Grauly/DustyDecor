package grauly.dustydecor.blockentity

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.geometry.AlertBeamsShape
import grauly.dustydecor.geometry.BacksideBeamShape
import grauly.dustydecor.geometry.BiPlaneShape
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

class TallCageLampBlockEntityRenderer(
    private val blockRenderContext: BlockEntityRendererFactory.Context
) : BlockEntityRenderer<TallCageLampBlockEntity> {

    private val rotationPerTick: Double = PI / 20

    override fun render(
        entity: TallCageLampBlockEntity,
        tickProgress: Float,
        matrices: MatrixStack,
        light: Int,
        overlay: Int,
        cameraPos: Vec3d,
        crumblingOverlayCommand: ModelCommandRenderer.CrumblingOverlayCommand?,
        orderedRenderCommandQueue: OrderedRenderCommandQueue
    ) {
        if (!entity.shouldShowBeams()) return
        val time = entity.age + tickProgress
        val rotation = Quaternionf()
            .rotateTo(Vector3f(0f, 1f, 0f), entity.getRotationDirection().toVector3f())
            .mul(Quaternionf().rotateY((time * rotationPerTick).toFloat()))
        val offset = entity.pos.toCenterPos().subtract(cameraPos)
            .add(entity.getRotationDirection().multiply(-3/16.0))

        val betterRotation = Quaternionf()
            .rotationZ((PI / 2).toFloat())
            .mul(Quaternionf().rotateX((time * rotationPerTick).toFloat()))
            .mul(Quaternionf().rotateY((PI/2).toFloat()))

        matrices.push()
        orderedRenderCommandQueue.submitCustom(
            matrices,
            RenderLayer.getBeaconBeam(Identifier.of(DustyDecorMod.MODID, "textures/block/cage_lamp_beam.png"), true)
        ) { matrixStack, vertexConsumer ->
            AlertBeamsShape
                .getTransformed(offset, rotation = rotation)
                .apply(vertexConsumer, Vec2f(0f, 0f), Vec2f(1f, 1f)) {
                    it.color(entity.color).light(light).normal(0f, 1f, 0f).overlay(OverlayTexture.DEFAULT_UV)
                }
        }
        matrices.pop()
        //TODO: flash particle if looking into the lamp
    }

}