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
) : BlockEntityRenderer<TallCageLampBlockEntity> {

    private val rotationPerTick: Double = PI / 20
    private val blindingThreshold: Double = 0.02
    private val maxBlindingDistance: Double = 6.0

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
        val offsetWorldPos = entity.pos.toCenterPos().add(entity.getRotationDirection().multiply(-3 / 16.0))
        val camRelativeOffset = offsetWorldPos.subtract(cameraPos)
        val rotation = Quaternionf()
            .rotateTo(Vector3f(0f, 1f, 0f), entity.getRotationDirection().toVector3f())
            .mul(Quaternionf().rotateY((time * rotationPerTick).toFloat()))

        val blindingTestVector = Vector3f(1f, 0f, 0f).rotate(rotation)
        val blindingThreshold = 0.03

        if (camRelativeOffset.lengthSquared() < maxBlindingDistance.pow(2)) {
            if (
                camRelativeOffset.normalize().squaredDistanceTo(Vec3d(blindingTestVector)) < blindingThreshold.pow(2) ||
                camRelativeOffset.normalize().squaredDistanceTo(Vec3d(blindingTestVector.negate())) < blindingThreshold.pow(2)
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

        matrices.push()
        orderedRenderCommandQueue.submitCustom(
            matrices,
            RenderLayer.getBeaconBeam(Identifier.of(DustyDecorMod.MODID, "textures/block/cage_lamp_beam.png"), true)
        ) { matrixStack, vertexConsumer ->
            AlertBeamsShape
                .getTransformed(camRelativeOffset, rotation = rotation)
                .apply(vertexConsumer, Vec2f(0f, 0f), Vec2f(1f, 1f)) {
                    it.color(entity.color).light(light).normal(0f, 1f, 0f).overlay(OverlayTexture.DEFAULT_UV)
                }
        }
        matrices.pop()
    }

}