package grauly.dustydecor

import grauly.dustydecor.block.TallCageLampBlock
import grauly.dustydecor.block.VacPipeBlock
import grauly.dustydecor.block.VentBlock
import grauly.dustydecor.block.VentCoverBlock
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

    val TALL_CAGE_LAMPS: List<TallCageLampBlock> = DyeUtils.COLOR_ORDER.map {
        val id = "${it.id}_tall_cage_lamp"
        (registerBlock(::TallCageLampBlock, id, Settings.copy(Blocks.IRON_BARS)) as TallCageLampBlock)
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