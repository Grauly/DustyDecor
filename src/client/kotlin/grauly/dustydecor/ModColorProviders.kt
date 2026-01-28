package grauly.dustydecor

import grauly.dustydecor.block.furniture.StoolBlock
import grauly.dustydecor.block.lamp.LightingFixtureBlock
import grauly.dustydecor.util.DyeUtils
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView
import java.awt.Color

object ModColorProviders {
    fun init() {
        registerCageLamps()
        registerSingleColorBlocks()
    }

    private fun registerSingleColorBlocks() {
        DyeUtils.COLOR_ORDER.forEach {
            val lookupIndex = DyeUtils.COLOR_ORDER.indexOf(it)
            generateColoredStool(lookupIndex, ModBlocks.STOOLS)
        }
    }

    private fun generateColoredStool(lookupIndex: Int, blocks: List<StoolBlock>) {
        val color = DyeUtils.COLOR_ORDER[lookupIndex].entityColor
        ColorProviderRegistry.BLOCK.register(
            { state: BlockState, blockRenderView: BlockRenderView?, blockPos: BlockPos?, tintIndex: Int ->
                return@register color
            },
            blocks[lookupIndex]
        )
    }

    private fun registerCageLamps() {
        DyeUtils.COLOR_ORDER.forEach {
            val lookupIndex = DyeUtils.COLOR_ORDER.indexOf(it)
            generateColoredCageLamp(lookupIndex, ModBlocks.TALL_CAGE_LAMPS)
            generateColoredCageLamp(lookupIndex, ModBlocks.WIDE_CAGE_LAMPS)
            generateColoredCageLamp(lookupIndex, ModBlocks.ALARM_CAGE_LAMPS)
            generateColoredCageLamp(lookupIndex, ModBlocks.TUBE_LAMPS)
        }
    }

    private fun generateColoredCageLamp(index: Int, lamps: List<Block>) {
        val color = DyeUtils.COLOR_ORDER[index].signColor
        val lowerColor = Color(DyeUtils.COLOR_ORDER[index].entityColor).darker().darker().darker().rgb
        ColorProviderRegistry.BLOCK.register(
            { state: BlockState, blockRenderView: BlockRenderView?, blockPos: BlockPos?, tintIndex: Int ->
                if (state.get(LightingFixtureBlock.LIT) != state.get(LightingFixtureBlock.INVERTED) &&
                    !state.get(LightingFixtureBlock.BROKEN)
                ) return@register color
                return@register lowerColor
            },
            lamps[index]
        )

    }
}