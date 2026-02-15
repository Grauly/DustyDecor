package grauly.dustydecor

import grauly.dustydecor.component.BulkGoopSizeComponent
import grauly.dustydecor.component.ToolComponents
import grauly.dustydecor.component.WrenchDataComponent
import grauly.dustydecor.item.BulkVoidGoopItem
import grauly.dustydecor.item.OutsideCrystalShardItem
import grauly.dustydecor.item.VacCapsuleItem
import grauly.dustydecor.util.DyeUtils
import net.minecraft.world.level.block.Block
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.util.Unit

object ModItems {

    /*
    Checklist for adding a new Item:
    - ItemDatagen Entry (optional for BlockItems)
    - ModCreativeModeTags Entry
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
        Properties()
            .component(ModDataComponentTypes.VOID_GOOP_SIZE, BulkGoopSizeComponent.DEFAULT)
            .attributes(
                ItemAttributeModifiers.builder()
                .add(
                    Attributes.BLOCK_INTERACTION_RANGE,
                    AttributeModifier(
                        Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "bulk_goop_place_range"),
                        2.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    ),
                    EquipmentSlotGroup.MAINHAND
                ).build())
    )
    val OUTSIDE_CRYSTAL_SHARD: Item = registerItem(::OutsideCrystalShardItem, "outside_crystal_shard")
    val SCREWDRIVER: Item = registerItem(
        ::Item,
        "screwdriver",
        Properties()
            .sword(ModToolMaterials.SCREWDRIVER_TOOL_MATERIAL, -0.5f, 2.0f)
            .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
            .component(ModDataComponentTypes.VENT_COVER_LOCK, ToolComponents.VENT_LOCK_TOGGLE.component)
            .component(ModDataComponentTypes.VAC_TUBE_WINDOW_TOGGLE, ToolComponents.VAC_TUBE_WINDOW_TOGGLE.component)
            .component(ModDataComponentTypes.LAMP_INVERSION, ToolComponents.LAMPS_INVERT.component)
    )
    val WRENCH: Item = registerItem(
        ::Item,
        "wrench",
        Properties()
            .sword(ModToolMaterials.WRENCH_TOOL_MATERIAL, 1.0f, -3.2f)
            .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
            .component(ModDataComponentTypes.WRENCH, WrenchDataComponent)
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

    val STOOLS: List<Item> = ModBlocks.STOOLS.map {
        val id = "${DyeUtils.COLOR_ORDER[ModBlocks.STOOLS.indexOf(it)]}_stool"
        registerBlockItem(it, id)
    }

    val CHAIRS: List<Item> = ModBlocks.CHAIRS.map {
        val id = "${DyeUtils.COLOR_ORDER[ModBlocks.CHAIRS.indexOf(it)]}_chair"
        registerBlockItem(it, id)
    }

    val SMALL_GLASS_TABLE: Item = registerBlockItem(ModBlocks.SMALL_GLASS_TABLE, "small_glass_table")
    val SMALL_GLASS_TABLES: List<Item> = ModBlocks.SMALL_GLASS_TABLES.map {
        val id = "small_${DyeUtils.COLOR_ORDER[ModBlocks.SMALL_GLASS_TABLES.indexOf(it)]}_glass_table"
        registerBlockItem(it, id)
    }
    val SMALL_GLASS_TABLE_FRAME: Item = registerBlockItem(ModBlocks.SMALL_GLASS_TABLE_FRAME, "small_glass_table_frame")

    private fun registerItem(
        itemFactory: (Properties) -> Item,
        id: String,
        settings: Properties = Properties(),
        namespace: String = DustyDecorMod.MODID
    ): Item {
        val key: ResourceKey<Item> = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(namespace, id))
        settings.setId(key)
        return Registry.register(BuiltInRegistries.ITEM, key, itemFactory.invoke(settings))
    }

    private fun registerBlockItem(
        block: Block,
        id: String,
        itemSettings: Properties = Properties(),
        namespace: String = DustyDecorMod.MODID
    ): Item {
        return registerItem(
            { settings: Properties -> BlockItem(block, settings) },
            id,
            settings = itemSettings,
            namespace
        )
    }

    fun init() {
        //[Space intentionally left blank]
    }
}