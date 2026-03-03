package grauly.dustydecor

import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import grauly.dustydecor.block.lamp.LightingFixtureBlock
import grauly.dustydecor.util.DyeUtils
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry
import net.minecraft.client.color.block.BlockTintSource
import net.minecraft.client.color.block.BlockTintSources
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.properties.Property
import java.awt.Color
import kotlin.collections.listOf

object ModBlockColors {
    fun init() {
        registerCageLamps()
        registerSingleColorBlocks()
    }

    private fun registerSingleColorBlocks() {
        DyeUtils.COLOR_ORDER.forEach {
            val lookupIndex = DyeUtils.COLOR_ORDER.indexOf(it)
            generateColoredFurniture(lookupIndex, ModBlocks.STOOLS)
            generateColoredFurniture(lookupIndex, ModBlocks.CHAIRS)
        }
    }

    private fun generateColoredFurniture(lookupIndex: Int, blocks: List<SingleFurnitureBlock>) {
        val color = DyeUtils.COLOR_ORDER[lookupIndex].textureDiffuseColor
        BlockColorRegistry.register(
            listOf(BlockTintSources.constant(color)),
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
        val color = DyeUtils.COLOR_ORDER[index].textColor
        val lowerColor = Color(DyeUtils.COLOR_ORDER[index].textureDiffuseColor).darker().darker().darker().rgb
        val lampColorSource = object : BlockTintSource {
            override fun color(state: BlockState): Int {
                if (state.getValue(LightingFixtureBlock.LIT) != state.getValue(LightingFixtureBlock.INVERTED) &&
                    !state.getValue(LightingFixtureBlock.BROKEN)
                ) return color
                return lowerColor
            }

            override fun relevantProperties(): Set<Property<*>> {
                return setOf(LightingFixtureBlock.LIT, LightingFixtureBlock.BROKEN, LightingFixtureBlock.INVERTED)
            }
        }
        BlockColorRegistry.register(
            listOf(lampColorSource),
            lamps[index]
        )
    }
}