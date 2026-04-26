package grauly.dustydecor.block.layered

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import grauly.dustydecor.ModItemTags
import grauly.dustydecor.mixin.FallingBlockEntityBlockStateAccessorMixin
import grauly.dustydecor.util.FloodFill
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import java.awt.Color
import kotlin.math.sqrt

class VoidGoopBlock(threshold: Int, settings: Properties) : LayerThresholdSpreadingBlock(threshold, settings.forceSolidOn()) {

    //TODO: add gazing interaction (haha, player go splat)
    //TODO: add eye shaped rain splashes
    //TODO: add outsider spawning in large enough pools
    //TODO: add anti destruction mechanics: tp away
    //TODO: find a way to massively discourage just tp-ing it into random caves
    //TODO: fix the two-layer stable states from being permanent

    override fun canBeReplaced(
        state: BlockState,
        context: BlockPlaceContext
    ): Boolean {
        if (context.itemInHand.`is`(ModItemTags.VOID_GOOP)) {
            if (state.getValue(LAYERS) < MAX_LAYERS) {
                return true
            }
        }
        return super.canBeReplaced(state, context)
    }

    override fun onDestroyedByFall(level: Level, pos: BlockPos, fallingBlockState: BlockState) {
        val evasionPosition = findEvasionPosition(level, pos, fallingBlockState.getValue(VELOCITY))
        if (evasionPosition != null) {
            falling(respawnFallingEntity(level, evasionPosition, fallingBlockState))
            displayTeleportTrail(level, pos, evasionPosition)
            return
        }
        val safeTeleportPosition = findSafeTeleportPosition(level, pos, fallingBlockState.getValue(LAYERS))
        if (safeTeleportPosition != null) {
            falling(respawnFallingEntity(level, safeTeleportPosition, fallingBlockState))
            displayTeleportTrail(level, pos, safeTeleportPosition)
            return
        }
        val unsafeTeleportPosition = findUnsafeTeleportPosition(level, pos, false)
        if (unsafeTeleportPosition != null) {
            falling(respawnFallingEntity(level, unsafeTeleportPosition, fallingBlockState))
            displayTeleportTrail(level, pos, unsafeTeleportPosition)
            return
        }
        val levelCeilingTeleportPosition = findLevelCeilingEscapePoint(level, pos)
        val finalTeleportLocation = findUnsafeTeleportPosition(level, levelCeilingTeleportPosition, true)
        falling(respawnFallingEntity(level, finalTeleportLocation ?: levelCeilingTeleportPosition, fallingBlockState))
        displayTeleportTrail(level, pos, finalTeleportLocation ?: levelCeilingTeleportPosition)
    }

    fun findEvasionPosition(level: Level, pos: BlockPos, velocity: Direction): BlockPos? {
        if (velocity != Direction.DOWN && level.isInWorldBounds(pos.relative(velocity)) && isFree(level.getBlockState(pos.relative(velocity)))) return pos.relative(velocity)
        val possibleDirections = Direction.entries.filter { it.axis.isHorizontal }.filter { direction ->
            val offsetPos = pos.relative(direction)
            level.isInWorldBounds(offsetPos) && isFree(level.getBlockState(offsetPos))
        }.map { pos.relative(it) }
        return possibleDirections.randomOrNull()
    }

    fun findSafeTeleportPosition(level: Level, origin: BlockPos, layers: Int): BlockPos? {
        val floodFill = FloodFill(origin)
        floodFill.flood(
            level,
            { levelAccess, checkPos, checkState -> canReplaceTarget(level, checkPos, checkState) },
        )
        floodFill.layers.forEach { layer ->
            layer.forEach layerIterator@{ pos ->
                if (!level.isInWorldBounds(pos)) return@layerIterator
                val localState = level.getBlockState(pos)
                if (!canBePut(level, pos, localState)) return@layerIterator
                if (!canJoinLayers(level, pos, localState)) return pos
                val existingLayers = localState.getValue(LAYERS)
                val futureLayers = existingLayers + layers
                if (futureLayers < MAX_LAYERS) return pos
                if (canReplaceTarget(level, pos.above(), level.getBlockState(pos.above()))) return pos
            }
        }
        return null
    }

    fun findUnsafeTeleportPosition(level: Level, origin: BlockPos, ignoreWorldBounds: Boolean): BlockPos? {
        val random = level.random
        val visited = mutableSetOf<BlockPos>()
        for (i in 0..12000) {
            val offset = Vec3i(
                random.nextInt(32) - random.nextInt(32),
                random.nextInt(32) - random.nextInt(32),
                random.nextInt(32) - random.nextInt(32)
            )
            if (offset.x == 0 && offset.z == 0) continue
            val prospectivePos = origin.offset(offset)
            if (visited.contains(prospectivePos)) continue
            visited.add(prospectivePos)
            if (!ignoreWorldBounds && !level.isInWorldBounds(prospectivePos)) continue
            if (isFree(level.getBlockState(prospectivePos))) return prospectivePos
        }
        return null
    }

    fun findLevelCeilingEscapePoint(level: Level, origin: BlockPos): BlockPos {
        val inWorld = level.worldBorder.isWithinBounds(origin)
        val workingPos = if (inWorld) origin else level.worldBorder.clampToBounds(origin)
        return BlockPos(workingPos.x, level.height + 15, workingPos.z)
    }

    fun displayTeleportTrail(level: Level, from: BlockPos, to: BlockPos) {
        if (level !is ServerLevel) return
        val distance = sqrt(from.distSqr(to))
        val particlesPerBlock = 5
        val totalParticles = (distance * particlesPerBlock).toInt()
        val variance = 0.2
        val particle = DustParticleOptions(Color(0,0,0).rgb, 1f)
        for (i in 0..totalParticles) {
            val delta = i / totalParticles.toDouble()
            val x = Mth.lerp(delta, from.x.toDouble(), to.x.toDouble())
            val y = Mth.lerp(delta, from.y.toDouble(), to.y.toDouble())
            val z = Mth.lerp(delta, from.z.toDouble(), to.z.toDouble())
            level.sendParticles(
                particle,
                x, y, z,
                1,
                variance, variance, variance,
                1.0
            )
        }
    }

    override fun codec(): MapCodec<out VoidGoopBlock> {
        return RecordCodecBuilder.mapCodec {
            it.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("threshold").forGetter { block -> block.threshold },
                propertiesCodec()
            ).apply(it, ::VoidGoopBlock)
        }
    }

    override fun getDustColor(state: BlockState, world: BlockGetter, pos: BlockPos): Int {
        return Color(0f, 0f, 0f, 1f).rgb
    }

    companion object {
        fun respawnFallingEntity(level: Level, pos: BlockPos, state: BlockState): FallingBlockEntity {
            val entity = FallingBlockEntity(
                EntityType.FALLING_BLOCK,
                level
            )
            entity.setPos(
                pos.x + 0.5,
                pos.y.toDouble(),
                pos.z + 0.5
            )
            ((entity as Any) as FallingBlockEntityBlockStateAccessorMixin).`dustydecor$setBlockState`(state)
            entity.blocksBuilding = true
            entity.deltaMovement = Vec3.ZERO
            entity.xo = pos.x.toDouble() + 0.5
            entity.yo = pos.y.toDouble()
            entity.zo = pos.z.toDouble() + 0.5
            entity.startPos = pos
            level.addFreshEntity(entity)
            return entity
        }
    }
}