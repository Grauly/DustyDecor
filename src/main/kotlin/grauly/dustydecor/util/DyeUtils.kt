package grauly.dustydecor.util

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.item.Items
import net.minecraft.util.DyeColor

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
}