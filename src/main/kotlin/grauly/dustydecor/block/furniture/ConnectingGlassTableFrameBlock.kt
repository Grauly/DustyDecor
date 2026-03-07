package grauly.dustydecor.block.furniture

import com.mojang.math.OctahedralGroup
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
        generateCollisionShapes()
        generateOutlineShapes()
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
            val block = ModBlocks.CONNECTING_GLASS_TABLES[GlassUtils.GLASS_PANE_ORDER.map { it.asItem() }
                .indexOf(itemStack.item)]
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

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return COLLISION_SHAPES[normalizeState(state)] ?: Shapes.block()
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return OUTLINE_SHAPES[normalizeState(state)] ?: Shapes.block()
    }

    override fun onProjectileHit(level: Level, state: BlockState, blockHit: BlockHitResult, projectile: Projectile) {
        //[Space intentionally left blank]
    }

    override fun attack(state: BlockState, level: Level, pos: BlockPos, player: Player) {
        //[Space intentionally left blank]
    }

    private fun generateOutlineShapes() {
        val base = column(14.0, 0.0, 15.0)
        for (state in stateDefinition.possibleStates) {
            if (state != normalizeState(state)) continue
            OUTLINE_SHAPES[state] = Shapes.or(
                base,
                mainDirectionConnection(state, Direction.NORTH),
                mainDirectionConnection(state, Direction.EAST),
                mainDirectionConnection(state, Direction.SOUTH),
                mainDirectionConnection(state, Direction.WEST),
                diagonalConnection(state, 0),
                diagonalConnection(state, 1),
                diagonalConnection(state, 2),
                diagonalConnection(state, 3),
            )
        }
    }

    private fun diagonalConnection(state: BlockState, indexOffset: Int): VoxelShape {
        val diagonalConnector = box(0.0, 0.0, 0.0, 1.0, 15.0, 1.0)
        val directionA = CONNECTION_DIRECTIONS[(indexOffset) % 4]
        val directionB = CONNECTION_DIRECTIONS[(indexOffset + 1) % 4]
        val property = DIRECTION_PROPERTIES[(indexOffset * 2 + 1) % 8].second
        return if (state.getValue(property) == FACE_CONNECTED &&
            state.getValue(getPropertyForDirection(directionA)!!) == FACE_CONNECTED &&
            state.getValue(getPropertyForDirection(directionB)!!) == FACE_CONNECTED
        ) {
            Shapes.rotate(diagonalConnector, ROTATION_MAP[directionB]!!)
        } else {
            Shapes.empty()
        }
    }

    private fun mainDirectionConnection(state: BlockState, direction: Direction): VoxelShape {
        val outsideConnector = box(1.0, 0.0, 0.0, 15.0, 15.0, 1.0)
        return if (state.getValue(getPropertyForDirection(direction)!!) == FACE_CONNECTED) {
            Shapes.rotate(outsideConnector, ROTATION_MAP[direction]!!)
        } else {
            Shapes.empty()
        }
    }

    private fun generateCollisionShapes() {
        for (state in stateDefinition.possibleStates) {
            if (state != normalizeState(state)) continue
            val connectionCount = CONNECTION_DIRECTIONS
                .map { getPropertyForDirection(it)!! }
                .map { if (state.getValue(it)) 1 else 0 }
                .sum()
            if (connectionCount == 4) {
                COLLISION_SHAPES[state] = Shapes.empty()
            } else if (connectionCount == 3) {
                val connectionDirection = CONNECTION_DIRECTIONS
                    .find { !state.getValue(getPropertyForDirection(it)!!) }
                COLLISION_SHAPES[state] = Shapes.rotate(FULL_FRAME_PART, ROTATION_MAP[connectionDirection]!!)
            } else if (connectionCount == 2) {
                val connections = CONNECTION_DIRECTIONS.filter { state.getValue(getPropertyForDirection(it)!!) }
                if (connections.first() == connections.last().opposite) {
                    COLLISION_SHAPES[state] = Shapes.rotate(NORTH_FACING_PARALLEL, ROTATION_MAP[connections.first()]!!)
                } else {
                    COLLISION_SHAPES[state] =
                        if (connections.first() == Direction.NORTH && connections.last() == Direction.WEST) {
                            Shapes.rotate(NORTH_EAST_OPEN_CORNER, ROTATION_MAP[connections.last()]!!)
                        } else {
                            Shapes.rotate(NORTH_EAST_OPEN_CORNER, ROTATION_MAP[connections.first()]!!)
                        }
                }
            } else if (connectionCount == 1) {
                val connectionDirection = CONNECTION_DIRECTIONS
                    .find { state.getValue(getPropertyForDirection(it)!!) }
                COLLISION_SHAPES[state] = Shapes.rotate(NORTH_OPEN_DEAD_END, ROTATION_MAP[connectionDirection]!!)
            } else if (connectionCount == 0) {
                COLLISION_SHAPES[state] = FULL_FRAME
            }
            listOf(0, 1, 2, 3).forEach { indexOffset ->
                if (state.getValue(DIRECTION_PROPERTIES[indexOffset * 2].second) == FACE_CONNECTED &&
                    state.getValue(DIRECTION_PROPERTIES[(indexOffset * 2 + 1) % 8].second) == !FACE_CONNECTED &&
                    state.getValue(DIRECTION_PROPERTIES[(indexOffset * 2 + 2) % 8].second) == FACE_CONNECTED
                ) {
                    COLLISION_SHAPES[state] = Shapes.or(
                        COLLISION_SHAPES[state]!!,
                        Shapes.rotate(INNER_CORNER, ROTATION_MAP[CONNECTION_DIRECTIONS[(indexOffset + 1) % 4]]!!)
                    )
                }
            }
        }
    }

    private fun normalizeState(state: BlockState): BlockState {
        return state.setValue(WATERLOGGED, false)
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
            Shapes.rotate(
                Shapes.or(
                    INNER_FRAME_PART,
                    Shapes.rotate(INNER_FRAME_PART, OctahedralGroup.BLOCK_ROT_Y_90)
                ), OctahedralGroup.BLOCK_ROT_Y_180
            )
        )
        val NORTH_OPEN_DEAD_END = Shapes.or(
            Shapes.rotate(POST, OctahedralGroup.BLOCK_ROT_Y_180),
            Shapes.rotate(POST, OctahedralGroup.BLOCK_ROT_Y_270),
            Shapes.rotate(INNER_FRAME_PART, OctahedralGroup.BLOCK_ROT_Y_270),
            OUTER_FRAME_PART,
            OUTER_FRAME_PART.move(13.0 / 16.0, 0.0, 0.0)
        )
        val NORTH_EAST_OPEN_CORNER = Shapes.or(
            Shapes.rotate(POST, OctahedralGroup.BLOCK_ROT_Y_270),
            Shapes.rotate(OUTER_FRAME_PART, OctahedralGroup.BLOCK_ROT_Y_90).move(0.0, 0.0, 13.0 / 16.0),
            OUTER_FRAME_PART,
        )
        val NORTH_FACING_PARALLEL = Shapes.rotate(
            Shapes.or(
                FULL_FRAME_PART,
                FULL_FRAME_PART.move(0.0, 0.0, 13.0 / 16.0)
            ), OctahedralGroup.BLOCK_ROT_Y_90
        )
        val INNER_CORNER = Shapes.or(
            POST,
            box(1.0, 0.0, 0.0, 2.0, 1.0, 1.0),
            box(1.0, 14.0, 0.0, 2.0, 15.0, 1.0),
            box(1.499, 12.0, 0.0, 1.501, 14.0, 1.0),
            box(0.0, 0.0, 1.0, 1.0, 1.0, 2.0),
            box(0.0, 14.0, 1.0, 1.0, 15.0, 2.0),
            box(0.0, 12.0, 1.499, 1.0, 14.0, 1.501)
        )
        var COLLISION_SHAPES: MutableMap<BlockState, VoxelShape> = mutableMapOf()
        var OUTLINE_SHAPES: MutableMap<BlockState, VoxelShape> = mutableMapOf()
        val ROTATION_MAP: Map<Direction, OctahedralGroup> = mapOf(
            Direction.NORTH to OctahedralGroup.IDENTITY,
            Direction.EAST to OctahedralGroup.BLOCK_ROT_Y_90,
            Direction.SOUTH to OctahedralGroup.BLOCK_ROT_Y_180,
            Direction.WEST to OctahedralGroup.BLOCK_ROT_Y_270,
        )
    }
}