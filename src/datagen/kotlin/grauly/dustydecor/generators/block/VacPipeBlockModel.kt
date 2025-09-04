package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.AbConnectableBlock
import grauly.dustydecor.block.ConnectionState
import grauly.dustydecor.block.VacPipeBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator
import net.minecraft.client.render.model.json.*
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.Identifier
import net.minecraft.util.math.AxisRotation
import net.minecraft.util.math.Direction

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
        blockStateModelGenerator.registerItemModel(
            ModItems.VAC_PIPE,
            Identifier.of(DustyDecorMod.MODID, "block/vac_pipe_inventory")
        )
    }

    private fun directionalConnector(
        direction: ConnectionState,
        connection: EnumProperty<ConnectionState>,
        creator: MultipartBlockModelDefinitionCreator
    ) {
        if (direction == ConnectionState.NONE) return //none states dont need to put anything down
        singleConnector(direction, connection, true, creator)
        singleConnector(direction, connection, false, creator)

        //windows for all partial windowed connections
        val windowFalseList: List<MultipartModelCondition> = AbConnectableBlock.connections
            .filter { it != connection }
            .map { VacPipeBlock.windowMap[it] }
            .map { MultipartModelConditionBuilder().put(it, false).build() }
        val anyNotWindow = combineOr(*windowFalseList.toTypedArray())
        val shouldNotHaveWindowCondition =
            MultipartModelConditionBuilder().put(VacPipeBlock.SHOULD_HAVE_WINDOW, false).build()
        val directionIsWindowCondition =
            MultipartModelConditionBuilder()
                .put(connection, direction)
                .put(VacPipeBlock.windowMap[connection], true)
                .build()
        creator.with(
            combineAnd(
                directionIsWindowCondition,
                anyNotWindow,
                shouldNotHaveWindowCondition
            ),
            VAC_CONNECTOR_WINDOW_ATTACHMENT
                .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction.direction])
                .apply(uvLock(false))
        )

        //windows for all non-straight windowed
        val windowTrueList: List<MultipartModelCondition> = AbConnectableBlock.connections
            .map { VacPipeBlock.windowMap[it] }
            .map { MultipartModelConditionBuilder().put(it, true).build() }
        val allWindowCondition = combineAnd(*windowTrueList.toTypedArray())
        val otherConnection = AbConnectableBlock.connections.first { it != connection }
        for (otherDirection in ConnectionState.entries.filter { it != direction }) {
            val directionCondition = MultipartModelConditionBuilder()
                .put(connection, direction)
                .put(otherConnection, otherDirection)
            //ignore straight connections only if they are supposed to have a window
            if (direction.direction!!.opposite == otherDirection.direction) {
                directionCondition.put(VacPipeBlock.SHOULD_HAVE_WINDOW, false)
            }
            creator.with(
                combineAnd(
                    directionCondition.build(),
                    allWindowCondition,
                    shouldNotHaveWindowCondition
                ),
                VAC_CONNECTOR_WINDOW_ATTACHMENT
                    .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction.direction])
                    .apply(uvLock(false))
            )
        }
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
        val allNoneCondition = combineAnd(*allNoneList.toTypedArray())
        listOf(true, false).forEach {
            creator.with(
                combineAnd(
                    allNoneCondition,
                    MultipartModelConditionBuilder().put(VacPipeBlock.SHOULD_HAVE_WINDOW, it).build()
                ),
                getVacCoreNone(it)
            )
        }
    }

    private fun singleDirectionalCore(creator: MultipartBlockModelDefinitionCreator) {
        listOf(true, false).forEach { shouldHaveWindow ->
            ConnectionState.entries.filter { it != ConnectionState.NONE }.forEach { currentDirection ->
                val allSame = MultipartModelConditionBuilder()
                AbConnectableBlock.connections.forEach { currentConnection ->
                    allSame.put(currentConnection, currentDirection)
                    val allNoneExceptCurrent =
                        MultipartModelConditionBuilder().put(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow)
                    AbConnectableBlock.connections.forEach {
                        allNoneExceptCurrent.put(
                            it,
                            if (it == currentConnection) currentDirection else ConnectionState.NONE
                        )
                    }
                    creator.with(
                        allNoneExceptCurrent,
                        getVacCoreNorth(shouldHaveWindow)
                            .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[currentDirection.direction])
                            .apply(uvLock(true))
                    )
                }
                creator.with(
                    allSame,
                    getVacCoreNorth(shouldHaveWindow)
                        .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[currentDirection.direction])
                        .apply(uvLock(true))
                )
            }

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

        listOf(true, false).forEach shouldHaveWindowLoop@{ shouldHaveWindow ->
            ConnectionState.entries.filter { it != ConnectionState.NONE }.forEach aLoop@{ aDirection ->
                ConnectionState.entries.filter { it != ConnectionState.NONE }.forEach bLoop@{ bDirection ->
                    if (aDirection == bDirection) return@bLoop
                    val isStraight = aDirection.direction!!.opposite == bDirection.direction!!
                    val appearanceConditon = MultipartModelConditionBuilder()
                        .put(AbConnectableBlock.connections[0], aDirection)
                        .put(AbConnectableBlock.connections[1], bDirection)
                        .put(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow)
                        .build()
                    if (!isStraight) {
                        creator.with(
                            appearanceConditon,
                            getMultiRotationCore(aDirection.direction!!, bDirection.direction!!, shouldHaveWindow)
                                .apply(uvLock(true))
                        )
                    } else {
                        val shouldHaveWindowCondition =
                            MultipartModelConditionBuilder().put(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow)
                                .build()
                        creator.with(
                            combineAnd(appearanceConditon, anyNotWindow),
                            VAC_CORE_STRAIGHT_OPAQUE
                                .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aDirection.fallDown!!])
                                .apply(uvLock(false))
                        )
                        creator.with(
                            combineAnd(appearanceConditon, allWindow, shouldHaveWindowCondition),
                            (if (shouldHaveWindow)
                                (VAC_CORE_STRAIGHT_TRANSPARENT_MAP[aDirection.fallDown!!]!!)
                            else
                                (VAC_CORE_STRAIGHT_OPAQUE.apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aDirection.fallDown!!])))
                                .apply(uvLock(false))
                        )
                    }
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
            (getConnector(isWindow))
                .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction.direction])
                .apply(uvLock(true))
        )
    }

    private fun getMultiRotationCore(
        aDirection: Direction,
        bDirection: Direction,
        shouldHaveWindow: Boolean
    ): WeightedVariant {
        val baseVariant =
            if (isReachableViaXRotation(aDirection) && isReachableViaXRotation(bDirection)) getVacCoreNorthTop(
                shouldHaveWindow
            ) else getVacCoreNorthEast(shouldHaveWindow)
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
        val xRotation =
            if (aDirection == Direction.DOWN || bDirection == Direction.DOWN) AxisRotation.R90 else AxisRotation.R270
        val yRotation =
            if (aDirection == Direction.WEST || bDirection == Direction.WEST) AxisRotation.R180 else AxisRotation.R0
        return ModelVariantOperator.ROTATION_X.withValue(xRotation)
            .then(ModelVariantOperator.ROTATION_Y.withValue(yRotation))
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

    private fun getVariant(id: String, transparent: Boolean): WeightedVariant {
        return BlockModelDatagen.singleVariant(id + if (transparent) "_transparent" else "_opaque")
    }

    private fun getVacCoreNorthTop(window: Boolean) = getVariant("block/vac_pipe_north_top_core", window)

    //private val VAC_CORE_NORTH_TOP_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_north_top_core_opaque")
    private fun getVacCoreNorthEast(window: Boolean) = getVariant("block/vac_pipe_north_east_core", window)

    //private val VAC_CORE_NORTH_EAST_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_north_east_core_opaque")
    private fun getVacCoreNorth(window: Boolean) = getVariant("block/vac_pipe_north_core", window)

    //private val VAC_CORE_NORTH_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_north_core_opaque")
    private fun getVacCoreNone(window: Boolean) = getVariant("block/vac_pipe_none_core", window)
    //private val VAC_CORE_NONE_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_none_core_opaque")


    private fun getConnector(window: Boolean): WeightedVariant = getVariant("block/vac_pipe_connector", window)
    //private val VAC_CONNECTOR_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_connector_opaque")
    //private val VAC_CONNECTOR_TRANSPARENT = BlockModelDatagen.singleVariant("block/vac_pipe_connector_transparent")

    private val VAC_CONNECTOR_WINDOW_ATTACHMENT =
        BlockModelDatagen.singleVariant("block/vac_pipe_connector_window_attachment")

    private val VAC_CORE_STRAIGHT_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_straight_core_opaque")
    private val VAC_CORE_STRAIGHT_TRANSPARENT_MAP = mapOf(
        ConnectionState.NORTH.fallDown to BlockModelDatagen.singleVariant("block/vac_pipe_straight_core_north_south_transparent"),
        ConnectionState.WEST.fallDown to BlockModelDatagen.singleVariant("block/vac_pipe_straight_core_west_east_transparent"),
        ConnectionState.UP.fallDown to BlockModelDatagen.singleVariant("block/vac_pipe_straight_core_up_down_transparent"),
    )
}
