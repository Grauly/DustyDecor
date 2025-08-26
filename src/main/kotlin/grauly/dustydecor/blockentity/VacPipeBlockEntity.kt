package grauly.dustydecor.blockentity

import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.AbConnectableBlock
import grauly.dustydecor.inventory.SidedSelfCompactingInventory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class VacPipeBlockEntity(
    pos: BlockPos?,
    state: BlockState?,
    override val items: DefaultedList<ItemStack> = DefaultedList.ofSize(1)
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_ENTITY, pos, state), SidedSelfCompactingInventory {

    override fun insertDirections(): Set<Direction> {
        val aDirection = cachedState.get(AbConnectableBlock.connections[1]).direction
        return if (aDirection != null) setOf(aDirection) else setOf()
    }

    override fun extractDirections(): Set<Direction> {
        val bDirection = cachedState.get(AbConnectableBlock.connections[1]).direction
        return if (bDirection != null) setOf(bDirection) else setOf()
    }

    override fun canPlayerUse(player: PlayerEntity?): Boolean = false

}