package grauly.dustydecor.util

import grauly.dustydecor.ModComponentTypes
import grauly.dustydecor.ModConventionalItemTags
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.item.ItemStack

object ToolUtils {
    fun isScrewdriver(stack: ItemStack): Boolean {
        if (stack.isIn(ModConventionalItemTags.SCREWDRIVER_TOOLS)) return true
        if (stack.components.contains(ModComponentTypes.SCREWDRIVER)) return true
        return false
    }

    fun isWrench(stack: ItemStack): Boolean {
        if (stack.isIn(ConventionalItemTags.WRENCH_TOOLS)) return true
        if (stack.components.contains(ModComponentTypes.WRENCH)) return true
        return false
    }
}