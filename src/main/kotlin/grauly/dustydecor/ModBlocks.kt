package grauly.dustydecor

import grauly.dustydecor.block.furniture.ChairBlock
import grauly.dustydecor.block.furniture.GlassTableBlock
import grauly.dustydecor.block.furniture.SingleFurnitureBlock
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
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.SoundType

object ModBlocks {

    /*
    Checklist for adding a new Block:
    - BlockItem in ModItems (follow item addition checklist)
    - BlockDatagen entry
    - Texture/Model
     */

    val VENT: Block = registerBlock(::VentBlock, "vent", Properties.ofFullCopy(Blocks.IRON_BLOCK))
    val VENT_COVER: Block =
        registerBlock(::VentCoverBlock, "vent_cover", Properties.ofFullCopy(Blocks.IRON_TRAPDOOR).randomTicks().noOcclusion())
    val VAC_PIPE: Block = registerBlock(::VacPipeBlock, "vac_pipe", Properties.ofFullCopy(Blocks.HOPPER))
    val VAC_PIPE_STATION: Block = registerBlock(::VacPipeStationBlock, "vac_pipe_station", Properties.ofFullCopy(Blocks.HOPPER))
    val VOID_GOOP: Block = registerBlock({ settings -> VoidGoopBlock(1, settings) }, "void_goop")

    val TALL_CAGE_LAMPS: List<TallCageLampBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.getName()}_tall_cage_lamp"
        (registerBlock(
            ::TallCageLampBlock,
            id,
            Properties.ofFullCopy(Blocks.LANTERN)
                .lightLevel(LightingFixtureBlock.getLightingFunction(3, 15))
                .randomTicks()
        ) as TallCageLampBlock)
    }

    val ALARM_CAGE_LAMPS: List<AlarmCageLampBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.getName()}_alarm_cage_lamp"
        (registerBlock(
            ::AlarmCageLampBlock,
            id,
            Properties.ofFullCopy(Blocks.LANTERN)
                .lightLevel(LightingFixtureBlock.getLightingFunction(3, 15))
                .randomTicks()
        ) as AlarmCageLampBlock)
    }

    val WIDE_CAGE_LAMPS: List<WideCageLampBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.getName()}_wide_cage_lamp"
        (registerBlock(
            ::WideCageLampBlock,
            id,
            Properties.ofFullCopy(Blocks.LANTERN)
                .lightLevel(LightingFixtureBlock.getLightingFunction(3, 15))
                .randomTicks()
        ) as WideCageLampBlock)
    }

    val TUBE_LAMPS: List<TubeLampBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.getName()}_tube_lamp"
        (registerBlock(
            ::TubeLampBlock,
            id,
            Properties.ofFullCopy(Blocks.LANTERN)
                .lightLevel(LightingFixtureBlock.getLightingFunction(3, 15))
                .randomTicks()
        ) as TubeLampBlock)
    }

    val STOOLS: List<StoolBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.getName()}_stool"
        (registerBlock(
            ::StoolBlock,
            id,
            Properties.of().noOcclusion().requiresCorrectToolForDrops().mapColor(it).sound(SoundType.LANTERN)
        ) as StoolBlock)
    }

    val CHAIRS: List<ChairBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.getName()}_chair"
        (registerBlock(
            ::ChairBlock,
            id,
            Properties.of().noOcclusion().requiresCorrectToolForDrops().mapColor(it).sound(SoundType.LANTERN)
        ) as ChairBlock)
    }

    val GLASS_TABLES: List<GlassTableBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.getName()}_glass_table"
        (registerBlock(
            ::GlassTableBlock,
            id,
            Properties.of().noOcclusion().requiresCorrectToolForDrops().mapColor(it).sound(SoundType.GLASS)
        ) as GlassTableBlock)
    }

    val GLASS_TABLE: GlassTableBlock = registerBlock(::GlassTableBlock,
        "glass_table",
        Properties.of().noOcclusion().requiresCorrectToolForDrops().mapColor(DyeColor.WHITE).sound(SoundType.GLASS)
    ) as GlassTableBlock

    private fun registerBlock(
        blockFactory: (Properties) -> Block,
        id: String,
        settings: Properties = Properties.of(),
        namespace: String = DustyDecorMod.MODID
    ): Block {
        val key: ResourceKey<Block> = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(namespace, id))
        settings.setId(key)
        return Registry.register(BuiltInRegistries.BLOCK, key, blockFactory.invoke(settings))
    }

    fun init() {
        //[Space intentionally left blank]
    }
}