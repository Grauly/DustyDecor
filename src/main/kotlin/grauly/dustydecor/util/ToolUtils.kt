package grauly.dustydecor.util

import grauly.dustydecor.ModDataComponentTypes
import net.minecraft.world.entity.player.Player
import net.minecraft.sounds.SoundSource
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

object ToolUtils {
    fun playToolSound(stack: ItemStack, pos: BlockPos, level: Level, exceptPlayer: Player?) {
        if (!stack.has(ModDataComponentTypes.TOOL_USE_SOUND)) return
        level.playSound(
            exceptPlayer,
            pos,
            stack.get(ModDataComponentTypes.TOOL_USE_SOUND)!!.sound,
            if (exceptPlayer == null) SoundSource.BLOCKS else SoundSource.PLAYERS
        )
    }
}