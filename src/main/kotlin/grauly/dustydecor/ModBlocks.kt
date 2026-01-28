package grauly.dustydecor

import grauly.dustydecor.block.furniture.StoolBlock
import grauly.dustydecor.block.lamp.AlarmCageLampBlock
import grauly.dustydecor.block.lamp.LightingFixtureBlock
import grauly.dustydecor.block.lamp.TallCageLampBlock
import grauly.dustydecor.block.lamp.TubeLampBlock
import grauly.dustydecor.block.lamp.WideCageLampBlock
import grauly.dustydecor.block.vacpipe.VacPipeBlock
import grauly.dustydecor.block.vacpipe.VacPipeStationBlock
import grauly.dustydecor.block.vent.VentBlock
import grauly.dustydecor.block.vent.VentCoverBlock
import grauly.dustydecor.block.voidgoop.VoidGoopBlock
import grauly.dustydecor.util.DyeUtils
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object ModBlocks {

    /*
    Checklist for adding a new Block:
    - BlockItem in ModItems (follow item addition checklist)
    - BlockDatagen entry
    - Texture/Model
     */

    val VENT: Block = registerBlock(::VentBlock, "vent", Settings.copy(Blocks.IRON_BLOCK))
    val VENT_COVER: Block =
        registerBlock(::VentCoverBlock, "vent_cover", Settings.copy(Blocks.IRON_TRAPDOOR).ticksRandomly().nonOpaque())
    val VAC_PIPE: Block = registerBlock(::VacPipeBlock, "vac_pipe", Settings.copy(Blocks.HOPPER))
    val VAC_PIPE_STATION: Block = registerBlock(::VacPipeStationBlock, "vac_pipe_station", Settings.copy(Blocks.HOPPER))
    val VOID_GOOP: Block = registerBlock({settings -> VoidGoopBlock(1, settings) }, "void_goop")

    val TALL_CAGE_LAMPS: List<TallCageLampBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.id}_tall_cage_lamp"
        (registerBlock(
            ::TallCageLampBlock,
            id,
            Settings.copy(Blocks.LANTERN)
                .luminance(LightingFixtureBlock.getLightingFunction(3, 15))
                .ticksRandomly()
        ) as TallCageLampBlock)
    }

    val ALARM_CAGE_LAMPS: List<AlarmCageLampBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.id}_alarm_cage_lamp"
        (registerBlock(
            ::AlarmCageLampBlock,
            id,
            Settings.copy(Blocks.LANTERN)
                .luminance(LightingFixtureBlock.getLightingFunction(3, 15))
                .ticksRandomly()
        ) as AlarmCageLampBlock)
    }

    val WIDE_CAGE_LAMPS: List<WideCageLampBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.id}_wide_cage_lamp"
        (registerBlock(
            ::WideCageLampBlock,
            id,
            Settings.copy(Blocks.LANTERN)
                .luminance(LightingFixtureBlock.getLightingFunction(3, 15))
                .ticksRandomly()
        ) as WideCageLampBlock)
    }

    val TUBE_LAMPS: List<TubeLampBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.id}_tube_lamp"
        (registerBlock(
            ::TubeLampBlock,
            id,
            Settings.copy(Blocks.LANTERN)
                .luminance(LightingFixtureBlock.getLightingFunction(3, 15))
                .ticksRandomly()
        ) as TubeLampBlock)
    }

    val STOOLS: List<StoolBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.id}_stool"
        (registerBlock(
            ::StoolBlock,
            id,
            Settings.copy(Blocks.IRON_BLOCK).nonOpaque()
        ) as StoolBlock)
    }

    private fun registerBlock(
        blockFactory: (Settings) -> Block,
        id: String,
        settings: Settings = Settings.create(),
        namespace: String = DustyDecorMod.MODID
    ): Block {
        val key: RegistryKey<Block> = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(namespace, id))
        settings.registryKey(key)
        return Registry.register(Registries.BLOCK, key, blockFactory.invoke(settings))
    }

    fun init() {
        //[Space intentionally left blank]
    }
}