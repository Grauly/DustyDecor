package grauly.dustydecor

import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Item.Settings
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object ModItems {

    val VENT: Item = registerBlockItem(ModBlocks.VENT, "vent")

    private fun registerItem(
        itemFactory: (Settings) -> Item,
        id: String,
        settings: Settings = Settings(),
        namespace: String = DustyDecorMod.MODID
    ): Item {
        val key: RegistryKey<Item> = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(namespace, id))
        settings.registryKey(key)
        return Registry.register(Registries.ITEM, key, itemFactory.invoke(settings))
    }

    private fun registerBlockItem(
        block: Block,
        id: String,
        itemSettings: Settings = Settings(),
        namespace: String = DustyDecorMod.MODID
    ): Item {
        return registerItem(
            { settings: Settings -> BlockItem(block, settings) },
            id,
            settings = itemSettings,
            namespace
        )
    }

    fun init() {
        //[Space intentionally left blank]
    }
}