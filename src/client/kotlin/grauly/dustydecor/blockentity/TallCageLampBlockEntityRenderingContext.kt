package grauly.dustydecor.blockentity

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState
import net.minecraft.util.math.Vec3d

//TODO: replace with FAPI alternative ASAP
data class TallCageLampBlockEntityRenderingContext(
    var shouldShowBeams: Boolean,
    var time: Float,
    var rotationAxis: Vec3d,
    var cameraPos: Vec3d,
    var color: Int,
    ): BlockEntityRenderState()