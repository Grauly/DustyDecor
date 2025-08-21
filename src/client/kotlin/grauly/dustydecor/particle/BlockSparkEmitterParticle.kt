package grauly.dustydecor.particle

import grauly.dustydecor.ModParticleTypes
import net.minecraft.client.particle.NoRenderParticle
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape

class BlockSparkEmitterParticle(
    clientWorld: ClientWorld,
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
        val pos: BlockPos = BlockPos.ofFloored(x, y, z)
        val collisionShape: VoxelShape = world.getBlockState(pos).getCollisionShape(world, pos).simplify()
        val faces = mutableListOf<Face>()
        collisionShape.forEachBox { minX, minY, minZ, maxX, maxY, maxZ ->
            faces.add(Face(Vec3d(minX, minY, minZ), Vec3d(minX, maxY, maxZ)))
            faces.add(Face(Vec3d(minX, minY, minZ), Vec3d(maxX, minY, maxZ)))
            faces.add(Face(Vec3d(minX, minY, minZ), Vec3d(maxX, maxY, minZ)))
            faces.add(Face(Vec3d(maxX, minY, minZ), Vec3d(maxX, maxY, maxZ)))
            faces.add(Face(Vec3d(minX, maxY, minZ), Vec3d(maxX, maxY, maxZ)))
            faces.add(Face(Vec3d(minX, minY, maxZ), Vec3d(maxX, maxY, maxZ)))
        }
        val faceAreaMap: MutableMap<Face, Double> = faces.associateWith { it.area() }.toMutableMap()
        val totalSurface: Double = faceAreaMap.values.reduce { acc, elem -> acc + elem }
        val areaPerParticle: Double = amount / totalSurface
        for (i in 1..amount) {
            val face = faceAreaMap.filter { it.value > 0.0 }.entries.random().key
            faceAreaMap[face]?.minus(areaPerParticle)
            val point = face.randomPointOn(random)
            val xOffset = (random.nextDouble() - 0.5) * spread
            val yOffset = (random.nextDouble() - 0.5) * spread
            val zOffset = (random.nextDouble() - 0.5) * spread
            world.addParticleClient(
                if (random.nextDouble() > 0.4) ModParticleTypes.SPARK_PARTICLE_TYPE else ModParticleTypes.SMALL_SPARK_PARTICLE_TYPE,
                point.x, point.y, point.z, xDir + xOffset, yDir + yOffset, zDir + zOffset
            )
        }
        markDead()
    }

    private class Face(
        val minPos: Vec3d,
        val maxPos: Vec3d,
        val adjusted: Vec3d = maxPos.subtract(minPos)
    ) {
        fun area(): Double {
            val x = if (adjusted.x == 0.0) 1.0 else adjusted.x
            val y = if (adjusted.y == 0.0) 1.0 else adjusted.y
            val z = if (adjusted.z == 0.0) 1.0 else adjusted.z
            return x * y * z
        }

        fun randomPointOn(random: Random): Vec3d {
            val x = if (adjusted.x == 0.0) 0.0 else adjusted.x * random.nextDouble()
            val y = if (adjusted.y == 0.0) 0.0 else adjusted.y * random.nextDouble()
            val z = if (adjusted.z == 0.0) 0.0 else adjusted.z * random.nextDouble()
            return minPos.add(x, y, z)
        }
    }

}