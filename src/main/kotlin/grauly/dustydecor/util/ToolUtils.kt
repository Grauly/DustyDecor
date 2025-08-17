package grauly.dustydecor.util

import grauly.dustydecor.ModComponentTypes
import grauly.dustydecor.ModConventionalItemTags
import grauly.dustydecor.ModSoundEvents
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object ToolUtils {
    fun isScrewdriver(stack: ItemStack): Boolean {
        if (stack.isIn(ModConventionalItemTags.SCREWDRIVER_TOOLS)) return true
        if (stack.components.contains(ModComponentTypes.SCREWDRIVER)) return true
        return false
    }

    fun playScrewdriverSound(world: World, pos: BlockPos, player: PlayerEntity? = null) {
        playToolSound(world, pos, ModSoundEvents.ITEM_SCREWDRIVER_USE, player)
    }

    fun isWrench(stack: ItemStack): Boolean {
        if (stack.isIn(ConventionalItemTags.WRENCH_TOOLS)) return true
        if (stack.components.contains(ModComponentTypes.WRENCH)) return true
        return false
    }

    fun playWrenchSound(world: World, pos: BlockPos, player: PlayerEntity? = null) {
        playToolSound(world, pos, ModSoundEvents.ITEM_WRENCH_USE, player)
    }

    private fun playToolSound(world: World, pos: BlockPos, soundEvent: SoundEvent, player: PlayerEntity?) {
        world.playSound(
            player,
            pos,
            soundEvent,
            if (player == null) SoundCategory.BLOCKS else SoundCategory.PLAYERS
        )
    }
}