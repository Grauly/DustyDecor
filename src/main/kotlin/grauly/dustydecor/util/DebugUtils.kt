package grauly.dustydecor.util

import net.minecraft.core.BlockPos

object DebugUtils {
    const val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val nameMap: MutableMap<BlockPos, String> = mutableMapOf()
    fun nameBlockPos(pos: BlockPos): String = nameMap.computeIfAbsent(pos, {
        "${letters[nameMap.size % letters.length]}${nameMap.size / letters.length}"
    })
}