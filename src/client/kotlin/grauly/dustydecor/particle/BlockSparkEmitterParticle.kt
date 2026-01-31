package grauly.dustydecor.particle

import grauly.dustydecor.ModParticleTypes
import net.minecraft.client.particle.NoRenderParticle
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.shapes.VoxelShape

class BlockSparkEmitterParticle(
    clientWorld: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    private val xDir: Double,
    private val yDir: Double,
    private val zDir: Double,
    private val spread: Double,
    private val amount: Int
) : NoRenderParticle(clientWorld, x, y, z, xDir, yDir, zDir) {

    override fun tick() {
        val pos: BlockPos = BlockPos.containing(x, y, z)
        val collisionShape: VoxelShape = level.getBlockState(pos).getCollisionShape(level, pos)
        val faces = getFaceRepresentation(collisionShape)
        if (faces.isEmpty()) {
            this.remove()
            return
        }
        val faceAreaMap: MutableMap<Face, Double> = faces.associateWith { it.area() }.toMutableMap()
        val totalSurface: Double = faceAreaMap.values.reduce { acc, elem -> acc + elem }
        val areaPerParticle: Double = amount / totalSurface
        for (i in 1..amount) {
            val face = faceAreaMap.filter { it.value > 0.0 }.entries.random().key
            faceAreaMap[face]?.minus(areaPerParticle)
            val point = face.randomPointOn(random).add(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            val xOffset = (random.nextDouble() - 0.5) * spread
            val yOffset = (random.nextDouble() - 0.5) * spread
            val zOffset = (random.nextDouble() - 0.5) * spread
            level.addParticle(
                if (random.nextDouble() > 0.4) ModParticleTypes.SPARK_PARTICLE else ModParticleTypes.SMALL_SPARK_PARTICLE,
                point.x, point.y, point.z, xDir + xOffset, yDir + yOffset, zDir + zOffset
            )
        }
        remove()
    }

    private fun getFaceRepresentation(shape: VoxelShape): List<Face> {
        val lookup = SHAPE_FACE_CACHE[shape]
        if (lookup != null) return lookup
        val faces = mutableListOf<Face>()
        shape.forAllBoxes { minX, minY, minZ, maxX, maxY, maxZ ->
            faces.add(Face(Vec3(minX, minY, minZ), Vec3(minX, maxY, maxZ)))
            faces.add(Face(Vec3(minX, minY, minZ), Vec3(maxX, minY, maxZ)))
            faces.add(Face(Vec3(minX, minY, minZ), Vec3(maxX, maxY, minZ)))
            faces.add(Face(Vec3(maxX, minY, minZ), Vec3(maxX, maxY, maxZ)))
            faces.add(Face(Vec3(minX, maxY, minZ), Vec3(maxX, maxY, maxZ)))
            faces.add(Face(Vec3(minX, minY, maxZ), Vec3(maxX, maxY, maxZ)))
        }
        SHAPE_FACE_CACHE[shape] = faces
        return faces
    }

    private class Face(
        val minPos: Vec3,
        val maxPos: Vec3,
        val adjusted: Vec3 = maxPos.subtract(minPos)
    ) {
        fun area(): Double {
            val x = if (adjusted.x == 0.0) 1.0 else adjusted.x
            val y = if (adjusted.y == 0.0) 1.0 else adjusted.y
            val z = if (adjusted.z == 0.0) 1.0 else adjusted.z
            return x * y * z
        }

        fun randomPointOn(random: RandomSource): Vec3 {
            val x = if (adjusted.x == 0.0) 0.0 else adjusted.x * random.nextDouble()
            val y = if (adjusted.y == 0.0) 0.0 else adjusted.y * random.nextDouble()
            val z = if (adjusted.z == 0.0) 0.0 else adjusted.z * random.nextDouble()
            return minPos.add(x, y, z)
        }
    }

    companion object {
        private val SHAPE_FACE_CACHE: MutableMap<VoxelShape, List<Face>> = mutableMapOf()
    }
}