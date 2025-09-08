package grauly.dustydecor.util

import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

object VoxelShapesUtil {
    fun intCube(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int): VoxelShape {
        return VoxelShapes.cuboid(
            minX / 16.0,
            minY / 16.0,
            minZ / 16.0,
            maxX / 16.0,
            maxY / 16.0,
            maxZ / 16.0
        )
    }
}