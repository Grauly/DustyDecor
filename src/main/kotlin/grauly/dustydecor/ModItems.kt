package grauly.dustydecor

import net.minecraft.block.Block
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Item.Settings
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.util.Unit

object ModItems {

    val VENT: Item = registerBlockItem(ModBlocks.VENT, "vent")
    val VENT_COVER: Item = registerBlockItem(ModBlocks.VENT_COVER, "vent_cover")
    val VAC_PIPE: Item = registerBlockItem(ModBlocks.VAC_PIPE, "vac_pipe")
    val SCREWDRIVER: Item = registerItem(::Item, "screwdriver", Settings().sword(ModToolMaterials.SCREWDRIVER_TOOL_MATERIAL, -0.5f, 2.0f).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE))
    val WRENCH: Item = registerItem(::Item, "wrench", Settings().sword(ModToolMaterials.WRENCH_TOOL_MATERIAL, 1.0f, -3.2f).component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE))

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