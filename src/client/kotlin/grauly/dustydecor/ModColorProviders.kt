package grauly.dustydecor

import grauly.dustydecor.block.LightingFixtureBlock
import grauly.dustydecor.util.DyeUtils
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView
import java.awt.Color

object ModColorProviders {
    fun init() {
        registerCageLamps()
    }

    private fun registerCageLamps() {
        ModBlocks.TALL_CAGE_LAMPS.forEach {
            val lookupIndex = ModBlocks.TALL_CAGE_LAMPS.indexOf(it)
            val color = DyeUtils.COLOR_ORDER[lookupIndex].signColor
            val lowerColor = Color(DyeUtils.COLOR_ORDER[lookupIndex].entityColor).darker().darker().darker().rgb
            ColorProviderRegistry.BLOCK.register(
                { state: BlockState, blockRenderView: BlockRenderView?, blockPos: BlockPos?, tintIndex: Int ->
                    if (state.get(LightingFixtureBlock.LIT) != state.get(LightingFixtureBlock.INVERTED) &&
                        !state.get(LightingFixtureBlock.BROKEN)
                    ) return@register color
                    return@register lowerColor
                },
                it
            )
        }
    }
}