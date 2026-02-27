package grauly.dustydecor.block.furniture

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty

class ConnectingGlassTableBlock(properties: Properties) : GranularHorizontalConnectingBlock(properties) {

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(BROKEN, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(BROKEN)
    }

    companion object {
        val BROKEN = BooleanProperty.create("broken")
    }
}