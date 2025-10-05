package grauly.dustydecor

import grauly.dustydecor.component.BulkGoopSizeComponent
import grauly.dustydecor.component.ScrewdriverComponent
import grauly.dustydecor.component.WrenchComponent
import grauly.dustydecor.item.BulkVoidGoopItem
import grauly.dustydecor.item.OutsideCrystalShardItem
import grauly.dustydecor.item.VacCapsuleItem
import grauly.dustydecor.util.DyeUtils
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

    /*
    Checklist for adding a new Item:
    - ItemDatagen Entry (optional for BlockItems)
    - ModItemGroups Entry
    - Texture/Model
    - Recipes
     */

    val VENT: Item = registerBlockItem(ModBlocks.VENT, "vent")
    val VENT_COVER: Item = registerBlockItem(ModBlocks.VENT_COVER, "vent_cover")
    val VAC_PIPE: Item = registerBlockItem(ModBlocks.VAC_PIPE, "vac_pipe")
    val VAC_PIPE_STATION: Item = registerBlockItem(ModBlocks.VAC_PIPE_STATION, "vac_pipe_station")
    val VAC_CAPSULE: Item = registerItem(::VacCapsuleItem, "vac_capsule")
    val VOID_GOOP: Item = registerBlockItem(ModBlocks.VOID_GOOP, "void_goop")
    val BULK_VOID_GOOP: Item = registerItem(
        ::BulkVoidGoopItem,
        "bulk_void_goop",
        Settings()
            .component(ModComponentTypes.VOID_GOOP_SIZE, BulkGoopSizeComponent.DEFAULT)
            .attributeModifiers(AttributeModifiersComponent.builder()
                .add(
                    EntityAttributes.BLOCK_INTERACTION_RANGE,
                    EntityAttributeModifier(
                        Identifier.of(DustyDecorMod.MODID, "bulk_goop_place_range"),
                        2.0,
                        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    ),
                    AttributeModifierSlot.MAINHAND
                ).build())
    )
    val OUTSIDE_CRYSTAL_SHARD: Item = registerItem(::OutsideCrystalShardItem, "outside_crystal_shard")
    val SCREWDRIVER: Item = registerItem(
        ::Item,
        "screwdriver",
        Settings()
            .sword(ModToolMaterials.SCREWDRIVER_TOOL_MATERIAL, -0.5f, 2.0f)
            .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
            .component(ModComponentTypes.SCREWDRIVER, ScrewdriverComponent)
    )
    val WRENCH: Item = registerItem(
        ::Item,
        "wrench",
        Settings()
            .sword(ModToolMaterials.WRENCH_TOOL_MATERIAL, 1.0f, -3.2f)
            .component(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE)
            .component(ModComponentTypes.WRENCH, WrenchComponent)
    )

    val TALL_CAGE_LAMPS: List<Item> = ModBlocks.TALL_CAGE_LAMPS.map {
        val id = "${DyeUtils.COLOR_ORDER[ModBlocks.TALL_CAGE_LAMPS.indexOf(it)]}_tall_cage_lamp"
        registerBlockItem(it, id)
    }

    val ALARM_CAGE_LAMPS: List<Item> = ModBlocks.ALARM_CAGE_LAMPS.map {
        val id = "${DyeUtils.COLOR_ORDER[ModBlocks.ALARM_CAGE_LAMPS.indexOf(it)]}_alarm_cage_lamp"
        registerBlockItem(it, id)
    }

    val WIDE_CAGE_LAMPS: List<Item> = ModBlocks.WIDE_CAGE_LAMPS.map {
        val id = "${DyeUtils.COLOR_ORDER[ModBlocks.WIDE_CAGE_LAMPS.indexOf(it)]}_wide_cage_lamp"
        registerBlockItem(it, id)
    }

    val TUBE_LAMPS: List<Item> = ModBlocks.TUBE_LAMPS.map {
        val id = "${DyeUtils.COLOR_ORDER[ModBlocks.TUBE_LAMPS.indexOf(it)]}_tube_lamp"
        registerBlockItem(it, id)
    }

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