package grauly.dustydecor.blockentity

import grauly.dustydecor.ModBlockEntityTypes
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class VacPipeStationBlockEntity(
    pos: BlockPos?,
    state: BlockState?,
    override val items: DefaultedList<ItemStack> = DefaultedList.ofSize(3)
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_STATION_ENTITY, pos, state), SidedSelfCompactingInventory {
    override fun insertDirections(): Set<Direction> = setOf(Direction.UP)
    override fun extractDirections(): Set<Direction> = setOf(Direction.UP)
    override fun canPlayerUse(player: PlayerEntity?): Boolean = true

    override fun readData(view: ReadView) {
        super.readData(view)
        Inventories.readData(view, items)
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, items)
    }
}