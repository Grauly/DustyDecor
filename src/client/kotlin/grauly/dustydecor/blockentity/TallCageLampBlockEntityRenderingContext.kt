package grauly.dustydecor.blockentity

import net.minecraft.class_11954
import net.minecraft.util.math.Vec3d

data class TallCageLampBlockEntityRenderingContext(
    var shouldShowBeams: Boolean,
    var time: Float,
    var rotationAxis: Vec3d,
    var cameraPos: Vec3d,
    var color: Int,
    ): class_11954()