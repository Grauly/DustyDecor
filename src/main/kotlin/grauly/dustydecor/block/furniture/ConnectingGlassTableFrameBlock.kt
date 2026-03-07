package grauly.dustydecor.block.furniture

import com.mojang.math.OctahedralGroup
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.util.GlassUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ConnectingGlassTableFrameBlock(properties: Properties) : ConnectingBreakableBlock(properties) {

    init {
        generateShapes()
    }

    private fun generateShapes() {
        for (state in stateDefinition.possibleStates) {
            if (state != normalizeState(state)) continue
            val connectionCount = CONNECTION_DIRECTIONS
                .map { getPropertyForDirection(it)!! }
                .map { if (state.getValue(it)) 1 else 0 }
                .sum()
            if (connectionCount == 4) {
                SHAPES[state] = Shapes.empty()
                continue
            }
            if (connectionCount == 3) {
                val connectionDirection = CONNECTION_DIRECTIONS
                    .find { !state.getValue(getPropertyForDirection(it)!!) }
                SHAPES[state] = Shapes.rotate(FULL_FRAME_PART, ROTATION_MAP[connectionDirection]!!)
                continue
            }
            if (connectionCount == 2) {
                val connections = CONNECTION_DIRECTIONS.filter { state.getValue(getPropertyForDirection(it)!!) }
                if (connections.first() == connections.last().opposite) {
                    SHAPES[state] = Shapes.rotate(NORTH_FACING_PARALLEL, ROTATION_MAP[connections.first()]!!)
                    continue
                }
                SHAPES[state] = if (connections.first() == Direction.NORTH && connections.last() == Direction.WEST) {
                     Shapes.rotate(NORTH_EAST_OPEN_CORNER, ROTATION_MAP[connections.last()]!!)
                } else {
                    Shapes.rotate(NORTH_EAST_OPEN_CORNER, ROTATION_MAP[connections.first()]!!)
                }
                continue
            }
            if (connectionCount == 1) {
                val connectionDirection = CONNECTION_DIRECTIONS
                    .find { state.getValue(getPropertyForDirection(it)!!) }
                SHAPES[state] = Shapes.rotate(NORTH_OPEN_DEAD_END, ROTATION_MAP[connectionDirection]!!)
                continue
            }
            if (connectionCount == 0) {
                SHAPES[state] = FULL_FRAME
                continue
            }
        }
    }

    private fun normalizeState(state: BlockState): BlockState {
        return state.setValue(WATERLOGGED, false)
    }

    override fun useItemOn(
        itemStack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (GlassUtils.GLASS_PANE_ORDER.map { it.asItem() }.contains(itemStack.item)) {
            val block = ModBlocks.CONNECTING_GLASS_TABLES[GlassUtils.GLASS_PANE_ORDER.map { it.asItem() }.indexOf(itemStack.item)]
            val replaceState = replaceBlock(block, level, state, pos)
            itemStack.consume(1, player)
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
            level.playSound(
                null,
                pos,
                replaceState.soundType.placeSound,
                SoundSource.BLOCKS
            )
            return InteractionResult.SUCCESS
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        generateShapes()
        return SHAPES[normalizeState(state)] ?: Shapes.block()
    }

    override fun onProjectileHit(level: Level, state: BlockState, blockHit: BlockHitResult, projectile: Projectile) {
        //[Space intentionally left blank]
    }

    override fun attack(state: BlockState, level: Level, pos: BlockPos, player: Player) {
        //[Space intentionally left blank]
    }

    companion object {
        val POST = box(1.0, 0.0, 1.0, 2.0, 15.0, 2.0)
        val INNER_FRAME_PART = Shapes.or(
            box(1.0, 0.0, 1.0, 2.0, 1.0, 15.0),
            box(1.0, 14.0, 1.0, 2.0, 15.0, 15.0),
            box(1.499, 12.0, 2.0, 1.501, 14.0, 14.0)
        )
        val OUTER_FRAME_PART = Shapes.or(
            box(1.0, 0.0, 0.0, 2.0, 1.0, 15.0),
            box(1.0, 14.0, 0.0, 2.0, 15.0, 15.0),
            box(1.499, 12.0, 0.0, 1.501, 14.0, 14.0)
        )
        val FULL_FRAME_PART = Shapes.or(
            box(0.0, 0.0, 1.0, 16.0, 1.0, 2.0),
            box(0.0, 14.0, 1.0, 16.0, 15.0, 2.0),
            box(0.0, 12.0, 1.499, 16.0, 14.0, 1.501)
        )
        val FULL_FRAME = Shapes.or(
            POST,
            Shapes.rotate(POST, OctahedralGroup.BLOCK_ROT_Y_90),
            Shapes.rotate(POST, OctahedralGroup.BLOCK_ROT_Y_180),
            Shapes.rotate(POST, OctahedralGroup.BLOCK_ROT_Y_270),
            INNER_FRAME_PART,
            Shapes.rotate(INNER_FRAME_PART, OctahedralGroup.BLOCK_ROT_Y_90),
            Shapes.rotate(Shapes.or(
                INNER_FRAME_PART,
                Shapes.rotate(INNER_FRAME_PART, OctahedralGroup.BLOCK_ROT_Y_90)
            ), OctahedralGroup.BLOCK_ROT_Y_180)
        )
        val NORTH_OPEN_DEAD_END = Shapes.or(
            Shapes.rotate(POST, OctahedralGroup.BLOCK_ROT_Y_180),
            Shapes.rotate(POST, OctahedralGroup.BLOCK_ROT_Y_270),
            Shapes.rotate(INNER_FRAME_PART, OctahedralGroup.BLOCK_ROT_Y_270),
            OUTER_FRAME_PART,
            OUTER_FRAME_PART.move(13.0/16.0,0.0,0.0)
        )
        val NORTH_EAST_OPEN_CORNER = Shapes.or(
            Shapes.rotate(POST, OctahedralGroup.BLOCK_ROT_Y_270),
            Shapes.rotate(OUTER_FRAME_PART, OctahedralGroup.BLOCK_ROT_Y_90).move(0.0, 0.0, 13.0/16.0),
            OUTER_FRAME_PART,
        )
        val NORTH_FACING_PARALLEL = Shapes.rotate(Shapes.or(
            FULL_FRAME_PART,
            FULL_FRAME_PART.move(0.0,0.0,13.0/16.0)
        ), OctahedralGroup.BLOCK_ROT_Y_90)
        var SHAPES: MutableMap<BlockState, VoxelShape> = mutableMapOf()
        val ROTATION_MAP: Map<Direction, OctahedralGroup> = mapOf(
            Direction.NORTH to OctahedralGroup.IDENTITY,
            Direction.EAST to OctahedralGroup.BLOCK_ROT_Y_90,
            Direction.SOUTH to OctahedralGroup.BLOCK_ROT_Y_180,
            Direction.WEST to OctahedralGroup.BLOCK_ROT_Y_270,
        )
    }
}