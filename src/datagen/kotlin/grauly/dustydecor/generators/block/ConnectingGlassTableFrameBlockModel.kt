package grauly.dustydecor.generators.block

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.block.furniture.ConnectingGlassTableBlock
import grauly.dustydecor.block.furniture.GranularHorizontalConnectingBlock
import grauly.dustydecor.block.furniture.HorizontalConnectingBlock
import grauly.dustydecor.block.vacpipe.ConnectionState
import grauly.dustydecor.generators.BlockModelDatagen
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.blockstates.ConditionBuilder
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.core.Direction
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.state.properties.BooleanProperty

open class ConnectingGlassTableFrameBlockModel(
    private val blocks: List<ConnectingGlassTableBlock>,
    private val replacements: List<Identifier>,
    val basePath: String
) {
    val SINGLE_SIDE = BlockModelDatagen.singleVariant("$basePath/one_table_frame")
    val PARALLEL = BlockModelDatagen.singleVariant("$basePath/two_parallel_table_frame")
    val CORNER = BlockModelDatagen.singleVariant("$basePath/two_corner_table_frame")
    val DEAD_END = BlockModelDatagen.singleVariant("$basePath/three_table_frame")
    val FULL = BlockModelDatagen.singleVariant("$basePath/four_table_frame")
    val INNER_LEG = BlockModelDatagen.singleVariant("$basePath/one_inner_frame")

    fun get(blockStateModelGenerator: BlockModelGenerators) {
        blocks.forEach {
            createTable(it, replacements[blocks.indexOf(it)], blockStateModelGenerator)
        }
    }

    fun createTable(block: ConnectingGlassTableBlock, replaceTexture: Identifier, generator: BlockModelGenerators) {
        createBlock(block, replaceTexture, generator)
        createItem(block, replaceTexture, generator)
    }

    private fun createBlock(
        block: ConnectingGlassTableBlock,
        replaceTexture: Identifier,
        generator: BlockModelGenerators
    ) {

        val modelGenerator = MultiPartGenerator.multiPart(block)
        extraBlockSetup(
            block,
            replaceTexture,
            generator,
            modelGenerator
        )
        val fullCondition = ConditionBuilder()
        directions.forEach {
            fullCondition.term(
            HorizontalConnectingBlock.getPropertyForDirection(it)!!,
                !HorizontalConnectingBlock.FACE_CONNECTED
            )
        }
        modelGenerator.with(
            fullCondition,
            FULL
        )
        listOf(0, 1, 2, 3).forEach { indexShift ->
            oneConnection(
                directions[indexShift],
                modelGenerator
            )
            corner(
                directions[indexShift],
                directions[(indexShift + 1) % 4],
                modelGenerator
            )
            parallel(
                directions[indexShift],
                directions[(indexShift + 2) % 4],
                modelGenerator
            )
            singleSide(
                directions[indexShift],
                modelGenerator
            )
            innerCorner(
                directions[indexShift],
                GranularHorizontalConnectingBlock.DIRECTION_PROPERTIES[(indexShift * 2 + 1) % 8].second,
                directions[(indexShift + 1) % 4],
                modelGenerator
            )
        }
        generator.blockStateOutput.accept(modelGenerator)
    }

    protected open fun extraBlockSetup(
        block: ConnectingGlassTableBlock,
        replaceTexture: Identifier,
        generator: BlockModelGenerators,
        modelGenerator: MultiPartGenerator
    ) {}

    protected open fun singleSide(
        direction: Direction,
        modelGenerator: MultiPartGenerator
    ) {
        val condition = ConditionBuilder()
            .term(
                HorizontalConnectingBlock.getPropertyForDirection(direction)!!,
                !HorizontalConnectingBlock.FACE_CONNECTED
            )
        directions
            .filter { it != direction }
            .map { HorizontalConnectingBlock.getPropertyForDirection(it)!! }
            .forEach { condition.term(it, HorizontalConnectingBlock.FACE_CONNECTED) }
        modelGenerator.with(
            condition,
            SINGLE_SIDE.with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction]!!)
        )
    }

    protected open fun parallel(
        direction: Direction,
        direction2: Direction,
        modelGenerator: MultiPartGenerator
    ) {
        if (direction != ConnectionState.fromDirection(direction).fallDown) return //eliminate equivalent states
        val condition = ConditionBuilder()
            .term(
                HorizontalConnectingBlock.getPropertyForDirection(direction)!!,
                HorizontalConnectingBlock.FACE_CONNECTED
            )
            .term(
                HorizontalConnectingBlock.getPropertyForDirection(direction2)!!,
                HorizontalConnectingBlock.FACE_CONNECTED
            )
        val otherDirections = directions
            .filter { it != direction && it != direction2 }
        otherDirections
            .map { HorizontalConnectingBlock.getPropertyForDirection(it)!! }
            .forEach { condition.term(it, !HorizontalConnectingBlock.FACE_CONNECTED) }
        modelGenerator.with(
            condition,
            PARALLEL.with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction]!!)
        )
    }

    protected open fun corner(
        direction: Direction,
        direction2: Direction,
        modelGenerator: MultiPartGenerator
    ) {
        val condition = ConditionBuilder()
            .term(
                HorizontalConnectingBlock.getPropertyForDirection(direction)!!,
                HorizontalConnectingBlock.FACE_CONNECTED
            )
            .term(
                HorizontalConnectingBlock.getPropertyForDirection(direction2)!!,
                HorizontalConnectingBlock.FACE_CONNECTED
            )
        directions
            .filter { it != direction && it != direction2 }
            .map { HorizontalConnectingBlock.getPropertyForDirection(it)!! }
            .forEach { condition.term(it, !HorizontalConnectingBlock.FACE_CONNECTED) }
        modelGenerator.with(
            condition,
            CORNER.with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction]!!),
        )
    }

    protected open fun oneConnection(
        direction: Direction,
        modelGenerator: MultiPartGenerator
    ) {
        val condition = ConditionBuilder()
            .term(
                HorizontalConnectingBlock.getPropertyForDirection(direction)!!,
                HorizontalConnectingBlock.FACE_CONNECTED
            )
        directions
            .filter { it != direction }
            .map { HorizontalConnectingBlock.getPropertyForDirection(it)!! }
            .forEach { condition.term(it, !HorizontalConnectingBlock.FACE_CONNECTED) }
        modelGenerator.with(
            condition,
            DEAD_END.with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[direction]!!)
        )
    }

    protected open fun innerCorner(
        mainDirection: Direction,
        middleProperty: BooleanProperty,
        secondDirection: Direction,
        modelGenerator: MultiPartGenerator
    ) {
        modelGenerator.with(
            ConditionBuilder()
                .term(HorizontalConnectingBlock.getPropertyForDirection(mainDirection)!!, HorizontalConnectingBlock.FACE_CONNECTED)
                .term(HorizontalConnectingBlock.getPropertyForDirection(secondDirection)!!, HorizontalConnectingBlock.FACE_CONNECTED)
                .term(middleProperty, !HorizontalConnectingBlock.FACE_CONNECTED),
            INNER_LEG.with(BlockModelDatagen.NORTH_FACING_ROTATION_MAP[secondDirection]!!)
        )
    }

    protected open fun createItem(
        block: ConnectingGlassTableBlock,
        replaceTexture: Identifier,
        generator: BlockModelGenerators
    ) {
        generator.registerSimpleItemModel(block, Identifier.fromNamespaceAndPath(
            DustyDecorMod.MODID,
            "$basePath/four_table_frame"
        ))
    }

    private fun <T> cycleList(list: List<T>): List<T> {
        val last = list.last()
        val workingList = mutableListOf(last)
        workingList.addAll(list.subList(0, list.size - 1))
        return workingList.toList()
    }

    companion object {
        val directions = listOf(
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST,
        )
    }

}