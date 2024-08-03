package grauly.dustydecor

import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Item.Settings
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModItems {

    val VENT_ITEM : Item = registerBlockItem(ModBlocks.VENT, id = "vent")


    private fun registerItem(item: Item, id: String, namespace: String = DustyDecorMod.MODID) : Item {
        return Registry.register(Registries.ITEM, Identifier.of(namespace, id), item)
    }

    private fun registerBlockItem (
        block: Block,
        itemSettings: Settings = Settings(),
        id: String,
        namespace: String = DustyDecorMod.MODID
    ) : Item {
        return registerItem(BlockItem(block, itemSettings), id, namespace)
    }

    fun init() {
        //[Space intentionally left blank]
    }
}