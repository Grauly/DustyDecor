package grauly.dustydecor.generators.block

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.block.AbConnectableBlock
import grauly.dustydecor.block.ConnectionState
import grauly.dustydecor.block.VacPipeBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator
import net.minecraft.client.render.model.json.*
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.math.AxisRotation
import net.minecraft.util.math.Direction
import kotlin.math.abs
import kotlin.math.sign

object VacPipeBlockModel {
    fun get(blockStateModelGenerator: BlockStateModelGenerator) {
        val vacPipeModel = MultipartBlockModelDefinitionCreator.create(ModBlocks.VAC_PIPE)
        AbConnectableBlock.connections.forEach { connection ->
            ConnectionState.entries.forEach { direction ->
                directionalConnector(direction, connection, vacPipeModel)
            }
        }
        core(vacPipeModel)
        blockStateModelGenerator.blockStateCollector?.accept(vacPipeModel)
    }

    private fun directionalConnector(
        direction: ConnectionState,
        connection: EnumProperty<ConnectionState>,
        creator: MultipartBlockModelDefinitionCreator
    ) {
        if (direction == ConnectionState.NONE) return //none states dont need to put anything down
        singleConnector(direction, connection, true, creator)
        singleConnector(direction, connection, false, creator)
        return
        val windowFalseList: List<MultipartModelCondition> = AbConnectableBlock.connections
            .filter { it != connection }
            .map { VacPipeBlock.windowMap[it] }
            .map { MultipartModelConditionBuilder().put(it, false).build() }
        val anyWindowFalseCondition = combineOr(*windowFalseList.toTypedArray())
        val directionIsWindowCondition =
            MultipartModelConditionBuilder().put(VacPipeBlock.windowMap[connection], true).build()
        creator.with(
            combineAnd(
                directionIsWindowCondition,
                anyWindowFalseCondition
            ),
            VAC_CONNECTOR_WINDOW_ATTACHMENT
                .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction.direction])
                .apply(uvLock(false))
        )
    }

    private fun core(
        creator: MultipartBlockModelDefinitionCreator
    ) {
        blankCore(creator)
        singleDirectionalCore(creator)
        biDirectionalCore(creator)
    }

    private fun blankCore(creator: MultipartBlockModelDefinitionCreator) {
        val allNoneList = AbConnectableBlock.connections.map {
            MultipartModelConditionBuilder().put(it, ConnectionState.NONE).build()
        }
        creator.with(
            combineAnd(*allNoneList.toTypedArray()),
            VAC_CORE_NONE_OPAQUE
        )
    }

    private fun singleDirectionalCore(creator: MultipartBlockModelDefinitionCreator) {
        ConnectionState.entries.filter { it != ConnectionState.NONE }.forEach { currentDirection ->
            val allSame = MultipartModelConditionBuilder()
            AbConnectableBlock.connections.forEach { currentConnection ->
                allSame.put(currentConnection, currentDirection)
                val allNoneExceptCurrent = MultipartModelConditionBuilder()
                AbConnectableBlock.connections.forEach {
                    allNoneExceptCurrent.put(
                        it,
                        if (it == currentConnection) currentDirection else ConnectionState.NONE
                    )
                }
                creator.with(
                    allNoneExceptCurrent,
                    VAC_CORE_NORTH_OPAQUE
                        .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[currentDirection.direction])
                        .apply(uvLock(false))
                )
            }
            creator.with(
                allSame,
                VAC_CORE_NORTH_OPAQUE
                    .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[currentDirection.direction])
                    .apply(uvLock(false))
            )
        }
    }

    private fun biDirectionalCore(creator: MultipartBlockModelDefinitionCreator) {
        //a reverse of b -> straight connector
        //a window && b window -> transparent
        val allWindow = VacPipeBlock.windowStates.fold(MultipartModelConditionBuilder())
        { builder, element -> builder.put(element, true) }
            .build()
        val windowAllFalse = VacPipeBlock.windowStates.map { MultipartModelConditionBuilder().put(it, false).build() }
        val anyNotWindow = combineOr(*windowAllFalse.toTypedArray())

        ConnectionState.entries.filter { it != ConnectionState.NONE }.forEach aLoop@{ aDirection ->
            ConnectionState.entries.filter { it != ConnectionState.NONE }.forEach bLoop@{ bDirection ->
                if (aDirection == bDirection) return@bLoop
                val isStraight = aDirection.direction!!.opposite == bDirection.direction!!
                val directionCondition = MultipartModelConditionBuilder()
                    .put(AbConnectableBlock.connections[0], aDirection)
                    .put(AbConnectableBlock.connections[1], bDirection)
                    .build()
                if (!isStraight) {
                    creator.with(
                        directionCondition,
                        getMultiRotationCore(aDirection.direction!!, bDirection.direction!!)
                            .apply(uvLock(false))
                    )
                } else {
                    creator.with(
                        combineAnd(directionCondition, anyNotWindow),
                        VAC_CORE_STRAIGHT_OPAQUE
                            .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aDirection.fallDown!!])
                            .apply(uvLock(false))
                    )
                    creator.with(
                        combineAnd(directionCondition, allWindow),
                        VAC_CORE_STRAIGHT_TRANSPARENT
                            .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aDirection.fallDown!!])
                            .apply(uvLock(false))
                    )
                }
            }
        }
    }

    private fun singleConnector(
        direction: ConnectionState,
        connection: EnumProperty<ConnectionState>,
        isWindow: Boolean,
        creator: MultipartBlockModelDefinitionCreator
    ) {
        creator.with(
            MultipartModelConditionBuilder()
                .put(connection, direction)
                .put(VacPipeBlock.windowMap[connection], isWindow)
                .build(),
            (if (isWindow) VAC_CONNECTOR_TRANSPARENT else VAC_CONNECTOR_OPAQUE)
                .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction.direction])
                .apply(uvLock(true))
        )
    }

    private fun getMultiRotationCore(aDirection: Direction, bDirection: Direction): WeightedVariant {
        val baseVariant =
            if (isReachableViaXRotation(aDirection) && isReachableViaXRotation(bDirection)) VAC_CORE_NORTH_TOP_OPAQUE else VAC_CORE_NORTH_EAST_OPAQUE
        return baseVariant.apply(getMultiRotation(aDirection, bDirection))
    }

    //Problem: MC applies X rotation before Y rotation, always
    private fun getMultiRotation(aDirection: Direction, bDirection: Direction): ModelVariantOperator {
        if (aDirection == bDirection) return BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aDirection]!!
        if (isReachableViaXRotation(aDirection) && isReachableViaXRotation(bDirection))
            return rotatePlanarX(aDirection, bDirection)
        if (isReachableViaYRotation(aDirection) && isReachableViaYRotation(bDirection))
            return rotatePlanarY(aDirection, bDirection)
        return rotatePlanarZ(aDirection, bDirection)
    }

    private fun rotatePlanarZ(aDirection: Direction, bDirection: Direction): ModelVariantOperator {
        //assuming N-E Connector
        val xRotation = if (aDirection == Direction.DOWN || bDirection == Direction.DOWN) AxisRotation.R90 else AxisRotation.R270
        val yRotation = if (aDirection == Direction.WEST || bDirection == Direction.WEST) AxisRotation.R180 else AxisRotation.R0
        return ModelVariantOperator.ROTATION_X.withValue(xRotation).then(ModelVariantOperator.ROTATION_Y.withValue(yRotation))
    }

    private fun rotatePlanarY(aDirection: Direction, bDirection: Direction): ModelVariantOperator {
        //eliminate isomorphisms, and guarantee that the smaller rotation is aDirection
        if (yRotationDirections.indexOf(bDirection) < yRotationDirections.indexOf(aDirection))
            return rotatePlanarY(bDirection, aDirection)
        //assuming N-E Connector, aDirection is the smaller rotation of both
        if (aDirection == Direction.NORTH && bDirection == Direction.WEST) {
            return ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)
        }
        return ModelVariantOperator.ROTATION_Y.withValue(rotationOffsets[yRotationDirections.indexOf(aDirection)])
    }

    private fun rotatePlanarX(aDirection: Direction, bDirection: Direction): ModelVariantOperator {
        //eliminate isomorphisms, and guarantee that aRotation < bRotation
        if (xRotationDirections.indexOf(bDirection) < xRotationDirections.indexOf(aDirection)) {
            return rotatePlanarX(bDirection, aDirection)
        }
        //assuming N-T Connector, aDirection is the smaller rotation of both
        if (aDirection == Direction.NORTH && bDirection == Direction.UP) {
            return ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R0)
        }
        return ModelVariantOperator.ROTATION_X.withValue(rotationOffsets[xRotationDirections.indexOf(bDirection)])
    }

    private val rotationOffsets = listOf(AxisRotation.R0, AxisRotation.R90, AxisRotation.R180, AxisRotation.R270)
    private val xRotationDirections = listOf(Direction.NORTH, Direction.DOWN, Direction.SOUTH, Direction.UP) //CCW
    private val yRotationDirections = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST) //CW
    private fun isReachableViaXRotation(direction: Direction): Boolean = xRotationDirections.contains(direction)
    private fun isReachableViaYRotation(direction: Direction): Boolean = yRotationDirections.contains(direction)

    //again, bc I messed that up before: true means move the uv (lock the result to original rotation of the image), false means keep the original UV mapping
    private fun uvLock(lock: Boolean): ModelVariantOperator? {
        return ModelVariantOperator.UV_LOCK.withValue(lock)
    }

    private fun combineAnd(vararg conditions: MultipartModelCondition): MultipartModelCombinedCondition {
        return MultipartModelCombinedCondition(MultipartModelCombinedCondition.LogicalOperator.AND, conditions.asList())
    }

    private fun combineOr(vararg conditions: MultipartModelCondition): MultipartModelCombinedCondition {
        return MultipartModelCombinedCondition(MultipartModelCombinedCondition.LogicalOperator.OR, conditions.asList())
    }

    private val VAC_CORE_STRAIGHT_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_straight_core_opaque")
    private val VAC_CORE_STRAIGHT_TRANSPARENT =
        BlockModelDatagen.singleVariant("block/vac_pipe_straight_core_transparent")
    private val VAC_CORE_NORTH_TOP_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_north_top_core_opaque")
    private val VAC_CORE_NORTH_EAST_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_north_east_core_opaque")
    private val VAC_CORE_NORTH_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_north_core_opaque")
    private val VAC_CORE_NONE_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_none_core_opaque")
    private val VAC_CONNECTOR_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_connector_opaque")
    private val VAC_CONNECTOR_TRANSPARENT = BlockModelDatagen.singleVariant("block/vac_pipe_connector_transparent")
    private val VAC_CONNECTOR_WINDOW_ATTACHMENT =
        BlockModelDatagen.singleVariant("block/vac_pipe_connector_window_attachment")
}