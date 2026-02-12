package grauly.dustydecor.util

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.Blocks

object DyeUtils {
    val COLOR_ORDER = listOf(
        DyeColor.WHITE,
        DyeColor.LIGHT_GRAY,
        DyeColor.GRAY,
        DyeColor.BLACK,
        DyeColor.BROWN,
        DyeColor.RED,
        DyeColor.ORANGE,
        DyeColor.YELLOW,
        DyeColor.LIME,
        DyeColor.GREEN,
        DyeColor.CYAN,
        DyeColor.LIGHT_BLUE,
        DyeColor.PURPLE,
        DyeColor.MAGENTA,
        DyeColor.PINK
    )

    val DYE_ORDER = listOf(
        Items.WHITE_DYE,
        Items.LIGHT_GRAY_DYE,
        Items.GRAY_DYE,
        Items.BLACK_DYE,
        Items.BROWN_DYE,
        Items.RED_DYE,
        Items.ORANGE_DYE,
        Items.YELLOW_DYE,
        Items.LIME_DYE,
        Items.GREEN_DYE,
        Items.CYAN_DYE,
        Items.LIGHT_BLUE_DYE,
        Items.PURPLE_DYE,
        Items.MAGENTA_DYE,
        Items.PINK_DYE
    )

    val DYE_TAG_ORDER = listOf(
        ConventionalItemTags.WHITE_DYES,
        ConventionalItemTags.LIGHT_GRAY_DYES,
        ConventionalItemTags.GRAY_DYES,
        ConventionalItemTags.BLACK_DYES,
        ConventionalItemTags.BROWN_DYES,
        ConventionalItemTags.RED_DYES,
        ConventionalItemTags.ORANGE_DYES,
        ConventionalItemTags.YELLOW_DYES,
        ConventionalItemTags.LIME_DYES,
        ConventionalItemTags.GREEN_DYES,
        ConventionalItemTags.CYAN_DYES,
        ConventionalItemTags.LIGHT_BLUE_DYES,
        ConventionalItemTags.PURPLE_DYES,
        ConventionalItemTags.MAGENTA_DYES,
        ConventionalItemTags.PINK_DYES
    )

    val GLASS_ORDER = listOf(
        Blocks.WHITE_STAINED_GLASS,
        Blocks.LIGHT_GRAY_STAINED_GLASS,
        Blocks.GRAY_STAINED_GLASS,
        Blocks.BLACK_STAINED_GLASS,
        Blocks.BROWN_STAINED_GLASS,
        Blocks.RED_STAINED_GLASS,
        Blocks.ORANGE_STAINED_GLASS,
        Blocks.YELLOW_STAINED_GLASS,
        Blocks.LIME_STAINED_GLASS,
        Blocks.GREEN_STAINED_GLASS,
        Blocks.CYAN_STAINED_GLASS,
        Blocks.LIGHT_BLUE_STAINED_GLASS,
        Blocks.PURPLE_STAINED_GLASS,
        Blocks.MAGENTA_STAINED_GLASS,
        Blocks.PINK_STAINED_GLASS
    )

    val GLASS_PANE_ORDER = listOf(
        Blocks.WHITE_STAINED_GLASS_PANE,
        Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
        Blocks.GRAY_STAINED_GLASS_PANE,
        Blocks.BLACK_STAINED_GLASS_PANE,
        Blocks.BROWN_STAINED_GLASS_PANE,
        Blocks.RED_STAINED_GLASS_PANE,
        Blocks.ORANGE_STAINED_GLASS_PANE,
        Blocks.YELLOW_STAINED_GLASS_PANE,
        Blocks.LIME_STAINED_GLASS_PANE,
        Blocks.GREEN_STAINED_GLASS_PANE,
        Blocks.CYAN_STAINED_GLASS_PANE,
        Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
        Blocks.PURPLE_STAINED_GLASS_PANE,
        Blocks.MAGENTA_STAINED_GLASS_PANE,
        Blocks.PINK_STAINED_GLASS_PANE
    )
}