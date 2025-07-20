package grauly.dustydecor

import grauly.dustydecor.block.SideConnectableBlock
import net.minecraft.block.AbstractBlock
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object ModBlocks {

    val VENT: Block = registerBlock(::SideConnectableBlock, "vent")

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