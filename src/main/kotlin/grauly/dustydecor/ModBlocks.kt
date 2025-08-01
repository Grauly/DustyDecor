package grauly.dustydecor

import grauly.dustydecor.block.VentBlock
import grauly.dustydecor.block.VentCoverBlock
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object ModBlocks {

    val VENT: Block = registerBlock(::VentBlock, "vent", Settings.copy(Blocks.IRON_BLOCK))
    val VENT_COVER: Block = registerBlock(::VentCoverBlock, "vent_cover", Settings.copy(Blocks.IRON_TRAPDOOR).ticksRandomly().nonOpaque())

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