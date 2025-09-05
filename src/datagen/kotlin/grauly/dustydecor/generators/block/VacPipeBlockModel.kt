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
        generate(vacPipeModel)
        blockStateModelGenerator.blockStateCollector?.accept(vacPipeModel)
        blockStateModelGenerator.registerItemModel(
            ModItems.VAC_PIPE,
            Identifier.of(DustyDecorMod.MODID, "block/vac_pipe_inventory")
        )
    }

    private fun generate(creator: MultipartBlockModelDefinitionCreator) {
        ConnectionState.entries.forEach aDirection@{ aDirection ->
            //single connectors, this should take care of anything but the cores
            AbConnectableBlock.connections.forEach connections@{ connection ->
                listOf(true, false).forEach { hasWindow ->
                    singleWindowAttachment(aDirection, connection, creator)
                    singleConnector(aDirection, connection, hasWindow, creator)
                }
            }
            //from now on we are dealing with cores
            ConnectionState.entries.forEach bDirection@{ bDirection ->
                listOf(true, false).forEach shouldHaveWindow@{ shouldHaveWindow ->
                    if (aDirection == ConnectionState.NONE && bDirection == ConnectionState.NONE) {
                        blankCore(creator, shouldHaveWindow)
                        return@shouldHaveWindow
                    }
                    if (aDirection == ConnectionState.NONE || bDirection == ConnectionState.NONE || aDirection == bDirection) {
                        singleDirectionalCore(creator, aDirection, bDirection, shouldHaveWindow)
                        return@shouldHaveWindow
                    }
                    if (aDirection.direction!!.opposite == bDirection.direction) {
                        straightBiDirectionalCore(creator, aDirection, bDirection, shouldHaveWindow)
                        return@shouldHaveWindow
                    }
                    cornerBiDirectionalCore(creator, aDirection, bDirection, shouldHaveWindow)
                }
            }
        }
    }

    private fun singleWindowAttachment(
        aDirection: ConnectionState,
        connection: EnumProperty<ConnectionState>,
        creator: MultipartBlockModelDefinitionCreator
    ) {
        if (aDirection == ConnectionState.NONE) return
        creator.with(
            MultipartModelConditionBuilder()
                .put(connection, aDirection)
                .put(VacPipeBlock.windowMap[connection], true)
                .put(VacPipeBlock.SHOULD_HAVE_WINDOW, false),
            VAC_CONNECTOR_WINDOW_ATTACHMENT
                .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aDirection.direction])
        )
    }

    private fun singleConnector(
        direction: ConnectionState,
        connection: EnumProperty<ConnectionState>,
        isWindow: Boolean,
        creator: MultipartBlockModelDefinitionCreator
    ) {
        if (direction == ConnectionState.NONE) return
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

    private fun blankCore(creator: MultipartBlockModelDefinitionCreator, shouldHaveWindow: Boolean) {
        val allNoneList = AbConnectableBlock.connections.map {
            MultipartModelConditionBuilder().put(it, ConnectionState.NONE).build()
        }
        val allNoneCondition = combineAnd(*allNoneList.toTypedArray())
        creator.with(
            combineAnd(
                allNoneCondition,
                MultipartModelConditionBuilder().put(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow).build()
            ),
            getVacCoreNone(shouldHaveWindow)
        )
    }

    private fun singleDirectionalCore(
        creator: MultipartBlockModelDefinitionCreator,
        aDirection: ConnectionState,
        bDirection: ConnectionState,
        shouldHaveWindow: Boolean
    ) {
        val direction = if (aDirection == ConnectionState.NONE) bDirection.direction else aDirection.direction
        creator.with(
            MultipartModelConditionBuilder()
                .put(AbConnectableBlock.connections[0], aDirection)
                .put(AbConnectableBlock.connections[1], bDirection)
                .put(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow),
            getVacCoreNorth(shouldHaveWindow)
                .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction])
                .apply(uvLock(shouldHaveWindow))
        )
    }

    private fun straightBiDirectionalCore(
        creator: MultipartBlockModelDefinitionCreator,
        aDirection: ConnectionState,
        bDirection: ConnectionState,
        shouldHaveWindow: Boolean
    ) {
        creator.with(
            MultipartModelConditionBuilder()
                .put(AbConnectableBlock.connections[0], aDirection)
                .put(AbConnectableBlock.connections[1], bDirection)
                .put(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow),
            if (!shouldHaveWindow)
                VAC_CORE_STRAIGHT_OPAQUE
                    .apply(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aDirection.fallDown])
            else
                VAC_CORE_STRAIGHT_TRANSPARENT_MAP[aDirection.fallDown]!!
        )
    }

    private fun cornerBiDirectionalCore(
        creator: MultipartBlockModelDefinitionCreator,
        aDirection: ConnectionState,
        bDirection: ConnectionState,
        shouldHaveWindow: Boolean
    ) {
        val condition = MultipartModelConditionBuilder()
            .put(AbConnectableBlock.connections[0], aDirection)
            .put(AbConnectableBlock.connections[1], bDirection)
            .put(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow)

        val core = if (isReachableViaXRotation(aDirection.direction!!) && isReachableViaXRotation(bDirection.direction!!)) {
            //x axis = north top core
            getVacCoreNorthTop(shouldHaveWindow, aDirection.direction!!, bDirection.direction!!)
        } else if (isReachableViaYRotation(aDirection.direction!!) && isReachableViaYRotation(bDirection.direction!!)) {
            //y axis = north east core
            getVacCoreNorthEast(shouldHaveWindow).apply(rotatePlanarY(aDirection.direction!!, bDirection.direction!!))
        } else {
            //z axis = top east core
            getVacCoreEastTop(shouldHaveWindow, aDirection.direction!!, bDirection.direction!!)
        }

        creator.with(
            condition,
            core.apply(uvLock(shouldHaveWindow))
        )

    }

    private fun getVacCoreEastTop(shouldHaveWindow: Boolean, aDirection: Direction, bDirection: Direction): WeightedVariant {
        if (!shouldHaveWindow) {
            //opaque can still have the old handling, it worked well enough
            return getVacCoreNorthEast(false).apply(rotatePlanarZ(aDirection, bDirection))
        }
        if (zRotationDirections.indexOf(bDirection) < zRotationDirections.indexOf(aDirection))
            return getVacCoreEastTop(true, bDirection, aDirection)

        if (aDirection == Direction.UP && bDirection == Direction.WEST) {
            return getVacCoreEastTop(true, Direction.WEST)
        }
        return getVacCoreEastTop(true, aDirection)
    }

    private fun getVacCoreNorthTop(shouldHaveWindow: Boolean, aDirection: Direction, bDirection: Direction): WeightedVariant {
        if (!shouldHaveWindow) {
            //opaque can still have the old handling, it worked well enough
            return getVacCoreNorthTop(false).apply(rotatePlanarX(aDirection, bDirection))
        }
        if (xRotationDirections.indexOf(bDirection) < xRotationDirections.indexOf(aDirection)) {
            return getVacCoreNorthTop(true, bDirection, aDirection)
        }

        if (aDirection == Direction.NORTH && bDirection == Direction.UP) {
            return getVacCoreNorthTop(true, Direction.NORTH)
        }
        return getVacCoreNorthTop(true, bDirection)
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
    private val zRotationDirections = listOf(Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST) //CW
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
    private fun getVacCoreNorthEast(window: Boolean) = getVariant("block/vac_pipe_north_east_core", window)
    private fun getVacCoreNorth(window: Boolean) = getVariant("block/vac_pipe_north_core", window)
    private fun getVacCoreNone(window: Boolean) = getVariant("block/vac_pipe_none_core", window)
    private fun getConnector(window: Boolean): WeightedVariant = getVariant("block/vac_pipe_connector", window)

    private val VAC_CONNECTOR_WINDOW_ATTACHMENT =
        BlockModelDatagen.singleVariant("block/vac_pipe_connector_window_attachment")

    private fun getVacCoreEastTop(shouldHaveWindow: Boolean, primaryDirection: Direction) =
        getVariant("block/vac_pipe_east_top_core_${primaryDirection.id}", shouldHaveWindow)
    private fun getVacCoreNorthTop(shouldHaveWindow: Boolean, primaryDirection: Direction) =
        getVariant("block/vac_pipe_north_top_core_${primaryDirection.id}", shouldHaveWindow)

    private val VAC_CORE_STRAIGHT_OPAQUE = BlockModelDatagen.singleVariant("block/vac_pipe_straight_core_opaque")
    private val VAC_CORE_STRAIGHT_TRANSPARENT_MAP = mapOf(
        ConnectionState.NORTH.fallDown to BlockModelDatagen.singleVariant("block/vac_pipe_straight_core_north_south_transparent"),
        ConnectionState.WEST.fallDown to BlockModelDatagen.singleVariant("block/vac_pipe_straight_core_west_east_transparent"),
        ConnectionState.UP.fallDown to BlockModelDatagen.singleVariant("block/vac_pipe_straight_core_up_down_transparent"),
    )
}
