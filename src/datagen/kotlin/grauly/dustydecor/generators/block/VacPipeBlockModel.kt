package grauly.dustydecor.generators.block

import grauly.dustydecor.ModBlocks
import grauly.dustydecor.ModItems
import grauly.dustydecor.block.vacpipe.AbConnectableBlock
import grauly.dustydecor.block.vacpipe.ConnectionState
import grauly.dustydecor.block.vacpipe.VacPipeBlock
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.client.render.model.json.*
import net.minecraft.world.level.block.state.properties.EnumProperty
import com.mojang.math.Quadrant
import net.minecraft.core.Direction

object VacPipeBlockModel {
    fun get(blockStateModelGenerator: BlockModelGenerators) {
        val vacPipeModel = MultiPartGenerator.multiPart(ModBlocks.VAC_PIPE)
        generate(vacPipeModel)
        blockStateModelGenerator.blockStateOutput?.accept(vacPipeModel)
        blockStateModelGenerator.registerSimpleFlatItemModel(ModItems.VAC_PIPE)
    }

    private fun generate(creator: MultiPartGenerator) {
        ConnectionState.entries.forEach aDirection@{ aDirection ->
            //single connectors, this should take care of anything but the cores
            AbConnectableBlock.connections.forEach connections@{ connection ->
                listOf(true, false).forEach { hasWindow ->
                    windowConnector(aDirection, connection, creator)
                    listOf(true, false).forEach { shouldHaveWindow ->
                        connector(aDirection, connection, hasWindow, shouldHaveWindow, creator)
                    }
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

    private fun connector(
        aDirection: ConnectionState,
        connection: EnumProperty<ConnectionState>,
        hasWindow: Boolean,
        shouldHaveWindow: Boolean,
        creator: MultiPartGenerator
    ) {
        if (aDirection == ConnectionState.NONE) return
        if (!shouldHaveWindow && hasWindow) return
        creator.with(
            ConditionBuilder()
                .term(connection, aDirection)
                .term(VacPipeBlock.windowMap[connection], hasWindow)
                .term(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow),
            getConnector(hasWindow)
                .with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aDirection.direction])
                .with(uvLock(true))
        )
    }

    private fun windowConnector(
        aDirection: ConnectionState,
        connection: EnumProperty<ConnectionState>,
        creator: MultiPartGenerator
    ) {
        if (aDirection == ConnectionState.NONE) return
        val condition = ConditionBuilder()
                .term(connection, aDirection)
                .term(VacPipeBlock.windowMap[connection], true)
                .term(VacPipeBlock.SHOULD_HAVE_WINDOW, false)
        creator.with(
            condition,
            VAC_CONNECTOR_WINDOW_ATTACHMENT
                .with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aDirection.direction])
        )
        creator.with(
            condition,
            VAC_CONNECTOR_WINDOW_CONNECTOR
                .with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[aDirection.direction])
                .with(uvLock(true))
        )
    }

    private fun blankCore(creator: MultiPartGenerator, shouldHaveWindow: Boolean) {
        val allNoneList = AbConnectableBlock.connections.map {
            ConditionBuilder().term(it, ConnectionState.NONE).build()
        }
        val allNoneCondition = combineAnd(*allNoneList.toTypedArray())
        creator.with(
            combineAnd(
                allNoneCondition,
                ConditionBuilder().term(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow).build()
            ),
            getVacCoreNone(shouldHaveWindow)
        )
    }

    private fun singleDirectionalCore(
        creator: MultiPartGenerator,
        aDirection: ConnectionState,
        bDirection: ConnectionState,
        shouldHaveWindow: Boolean
    ) {
        val direction = if (aDirection == ConnectionState.NONE) bDirection.direction else aDirection.direction
        creator.with(
            ConditionBuilder()
                .term(AbConnectableBlock.connections[0], aDirection)
                .term(AbConnectableBlock.connections[1], bDirection)
                .term(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow),
            getVacCoreNorth(shouldHaveWindow)
                .with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction])
                .with(uvLock(true))
        )
    }

    private fun straightBiDirectionalCore(
        creator: MultiPartGenerator,
        aDirection: ConnectionState,
        bDirection: ConnectionState,
        shouldHaveWindow: Boolean
    ) {
        creator.with(
            ConditionBuilder()
                .term(AbConnectableBlock.connections[0], aDirection)
                .term(AbConnectableBlock.connections[1], bDirection)
                .term(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow),
            getVacCoreStraightMap(shouldHaveWindow)[aDirection.fallDown]
        )
    }

    private fun cornerBiDirectionalCore(
        creator: MultiPartGenerator,
        aDirection: ConnectionState,
        bDirection: ConnectionState,
        shouldHaveWindow: Boolean
    ) {
        val condition = ConditionBuilder()
            .term(AbConnectableBlock.connections[0], aDirection)
            .term(AbConnectableBlock.connections[1], bDirection)
            .term(VacPipeBlock.SHOULD_HAVE_WINDOW, shouldHaveWindow)

        val core = if (isReachableViaXRotation(aDirection.direction!!) && isReachableViaXRotation(bDirection.direction!!)) {
            //x axis = north top core
            getVacCoreNorthTop(shouldHaveWindow, aDirection.direction!!, bDirection.direction!!)
        } else if (isReachableViaYRotation(aDirection.direction!!) && isReachableViaYRotation(bDirection.direction!!)) {
            //y axis = north east core
            getVacCoreNorthEast(shouldHaveWindow).with(rotatePlanarY(aDirection.direction!!, bDirection.direction!!))
        } else {
            //z axis = top east core
            getVacCoreEastTop(shouldHaveWindow, aDirection.direction!!, bDirection.direction!!)
        }

        creator.with(
            condition,
            core.with(uvLock(true))
        )

    }

    private fun getVacCoreEastTop(shouldHaveWindow: Boolean, aDirection: Direction, bDirection: Direction): MultiVariant {
        if (zRotationDirections.indexOf(bDirection) < zRotationDirections.indexOf(aDirection))
            return getVacCoreEastTop(shouldHaveWindow, bDirection, aDirection)

        if (aDirection == Direction.UP && bDirection == Direction.WEST) {
            return getVacCoreEastTop(shouldHaveWindow, Direction.WEST)
        }
        return getVacCoreEastTop(shouldHaveWindow, aDirection)
    }

    private fun getVacCoreNorthTop(shouldHaveWindow: Boolean, aDirection: Direction, bDirection: Direction): MultiVariant {
        if (xRotationDirections.indexOf(bDirection) < xRotationDirections.indexOf(aDirection)) {
            return getVacCoreNorthTop(shouldHaveWindow, bDirection, aDirection)
        }

        if (aDirection == Direction.NORTH && bDirection == Direction.UP) {
            return getVacCoreNorthTop(shouldHaveWindow, Direction.NORTH)
        }
        return getVacCoreNorthTop(shouldHaveWindow, bDirection)
    }

    private fun rotatePlanarZ(aDirection: Direction, bDirection: Direction): VariantMutator {
        //assuming N-E Connector
        val xRotation =
            if (aDirection == Direction.DOWN || bDirection == Direction.DOWN) Quadrant.R90 else Quadrant.R270
        val yRotation =
            if (aDirection == Direction.WEST || bDirection == Direction.WEST) Quadrant.R180 else Quadrant.R0
        return VariantMutator.X_ROT.withValue(xRotation)
            .then(VariantMutator.Y_ROT.withValue(yRotation))
    }

    private fun rotatePlanarY(aDirection: Direction, bDirection: Direction): VariantMutator {
        //eliminate isomorphisms, and guarantee that the smaller rotation is aDirection
        if (yRotationDirections.indexOf(bDirection) < yRotationDirections.indexOf(aDirection))
            return rotatePlanarY(bDirection, aDirection)
        //assuming N-E Connector, aDirection is the smaller rotation of both
        if (aDirection == Direction.NORTH && bDirection == Direction.WEST) {
            return VariantMutator.Y_ROT.withValue(Quadrant.R270)
        }
        return VariantMutator.Y_ROT.withValue(rotationOffsets[yRotationDirections.indexOf(aDirection)])
    }

    private fun rotatePlanarX(aDirection: Direction, bDirection: Direction): VariantMutator {
        //eliminate isomorphisms, and guarantee that aRotation < bRotation
        if (xRotationDirections.indexOf(bDirection) < xRotationDirections.indexOf(aDirection)) {
            return rotatePlanarX(bDirection, aDirection)
        }
        //assuming N-T Connector, aDirection is the smaller rotation of both
        if (aDirection == Direction.NORTH && bDirection == Direction.UP) {
            return VariantMutator.X_ROT.withValue(Quadrant.R0)
        }
        return VariantMutator.X_ROT.withValue(rotationOffsets[xRotationDirections.indexOf(bDirection)])
    }

    private val rotationOffsets = listOf(Quadrant.R0, Quadrant.R90, Quadrant.R180, Quadrant.R270)
    private val xRotationDirections = listOf(Direction.NORTH, Direction.DOWN, Direction.SOUTH, Direction.UP) //CCW
    private val yRotationDirections = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST) //CW
    private val zRotationDirections = listOf(Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST) //CW
    private fun isReachableViaXRotation(direction: Direction): Boolean = xRotationDirections.contains(direction)
    private fun isReachableViaYRotation(direction: Direction): Boolean = yRotationDirections.contains(direction)

    //again, bc I messed that up before: true means move the uv (lock the result to original rotation of the image), false means keep the original UV mapping
    private fun uvLock(lock: Boolean): VariantMutator? {
        return VariantMutator.UV_LOCK.withValue(lock)
    }

    private fun combineAnd(vararg conditions: Condition): CombinedCondition {
        return CombinedCondition(CombinedCondition.Operation.AND, conditions.asList())
    }

    private fun combineOr(vararg conditions: Condition): CombinedCondition {
        return CombinedCondition(CombinedCondition.Operation.OR, conditions.asList())
    }

    private fun getVariant(id: String, transparent: Boolean): MultiVariant {
        return BlockModelDatagen.singleVariant(id + if (transparent) "_transparent" else "_opaque")
    }

    private fun getVacCoreNorthTop(window: Boolean) = getVariant("block/vac_pipe/vac_pipe_north_top_core", window)
    private fun getVacCoreNorthEast(window: Boolean) = getVariant("block/vac_pipe/vac_pipe_north_east_core", window)
    private fun getVacCoreNorth(window: Boolean) = getVariant("block/vac_pipe/vac_pipe_north_core", window)
    private fun getVacCoreNone(window: Boolean) = getVariant("block/vac_pipe/vac_pipe_none_core", window)
    private fun getConnector(window: Boolean): MultiVariant = getVariant("block/vac_pipe/vac_pipe_connector", window)

    private val VAC_CONNECTOR_WINDOW_ATTACHMENT =
        BlockModelDatagen.singleVariant("block/vac_pipe/vac_pipe_connector_window_attachment")

    private val VAC_CONNECTOR_WINDOW_CONNECTOR =
        BlockModelDatagen.singleVariant("block/vac_pipe/vac_pipe_connector_window_connector")

    private fun getVacCoreEastTop(shouldHaveWindow: Boolean, primaryDirection: Direction) =
        getVariant("block/vac_pipe/vac_pipe_east_top_core_${primaryDirection.name}", shouldHaveWindow)
    private fun getVacCoreNorthTop(shouldHaveWindow: Boolean, primaryDirection: Direction) =
        getVariant("block/vac_pipe/vac_pipe_north_top_core_${primaryDirection.name}", shouldHaveWindow)

    private fun getVacCoreStraightMap(window: Boolean) = mapOf(
        ConnectionState.NORTH.fallDown to getVariant("block/vac_pipe/vac_pipe_straight_core_north_south", window),
        ConnectionState.WEST.fallDown to getVariant("block/vac_pipe/vac_pipe_straight_core_west_east", window),
        ConnectionState.UP.fallDown to getVariant("block/vac_pipe/vac_pipe_straight_core_up_down", window),
    )
}
