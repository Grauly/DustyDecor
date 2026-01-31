package grauly.dustydecor.util

import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes

object VoxelShapesUtil {
    fun intCube(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int): VoxelShape {
        return Shapes.box(
            minX / 16.0,
            minY / 16.0,
            minZ / 16.0,
            maxX / 16.0,
            maxY / 16.0,
            maxZ / 16.0
        )
    }
}