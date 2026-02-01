package grauly.dustydecor.util

import grauly.dustydecor.ModDataComponentTypes
import grauly.dustydecor.ModConventionalItemTags
import grauly.dustydecor.ModSoundEvents
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvent
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

object ToolUtils {
    fun isScrewdriver(stack: ItemStack): Boolean {
        if (stack.`is`(ModConventionalItemTags.SCREWDRIVER_TOOLS)) return true
        if (stack.components.has(ModDataComponentTypes.SCREWDRIVER)) return true
        return false
    }

    fun playScrewdriverSound(world: Level, pos: BlockPos, player: Player? = null) {
        playToolSound(world, pos, ModSoundEvents.ITEM_SCREWDRIVER_USE, player)
    }

    fun isWrench(stack: ItemStack): Boolean {
        if (stack.`is`(ConventionalItemTags.WRENCH_TOOLS)) return true
        if (stack.components.has(ModDataComponentTypes.WRENCH)) return true
        return false
    }

    fun playWrenchSound(world: Level, pos: BlockPos, player: Player? = null) {
        playToolSound(world, pos, ModSoundEvents.ITEM_WRENCH_USE, player)
    }

    private fun playToolSound(world: Level, pos: BlockPos, soundEvent: SoundEvent, player: Player?) {
        world.playSound(
            player,
            pos,
            soundEvent,
            if (player == null) SoundSource.BLOCKS else SoundSource.PLAYERS
        )
    }
}