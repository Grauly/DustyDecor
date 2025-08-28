package grauly.dustydecor.blockentity

import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.AbConnectableBlock
import grauly.dustydecor.block.ConnectionState
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class VacPipeBlockEntity(
    pos: BlockPos?,
    state: BlockState?,
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_ENTITY, pos, state) {
    private val inventory: VacPipeInventory = VacPipeInventory(1, this)
    private val storageMap: Map<Direction, InventoryStorage> =
        Direction.entries.associateWith { InventoryStorage.of(inventory, it) }

    fun tick(world: World, pos: BlockPos, state: BlockState) {
        if (world.isClient) return
        if (inventory.isEmpty) return
        val followDirection = state.get(AbConnectableBlock.connections[1])
        if (followDirection == ConnectionState.NONE) return
        val targetBe = world.getBlockEntity(pos.offset(followDirection.direction))
        if (targetBe !is VacPipeBlockEntity && targetBe !is VacPipeStationBlockEntity) return
        val ownStorage = storageMap[followDirection.direction]
        val targetStorage =
            ItemStorage.SIDED.find(world, pos.offset(followDirection.direction), followDirection.direction?.opposite)
        if (ownStorage == null || targetStorage == null) return
        StorageUtil.move(ownStorage, targetStorage, { true }, 1, null)
    }

    fun getInsertDirection(): Direction? = cachedState.get(AbConnectableBlock.connections[0]).direction
    fun getExtractDirection(): Direction? = cachedState.get(AbConnectableBlock.connections[1]).direction

    fun getStorage(direction: Direction?): InventoryStorage? = storageMap[direction]
    fun getInventory(): Inventory = inventory

    override fun readData(view: ReadView?) {
        super.readData(view)
        Inventories.readData(view, inventory.heldStacks)
    }

    override fun writeData(view: WriteView?) {
        super.writeData(view)
        Inventories.writeData(view, inventory.heldStacks)
    }

    private class VacPipeInventory(
        size: Int,
        private val holder: VacPipeBlockEntity
    ) : SimpleInventory(size), SidedInventory {
        override fun isValid(slot: Int, stack: ItemStack?): Boolean {
            //TODO: filter for capsules only
            return super<SidedInventory>.isValid(slot, stack)
        }

        override fun getAvailableSlots(side: Direction?): IntArray {
            val fullAccessArray: IntArray = getHeldStacks().indices.toList().toIntArray()
            if (side == holder.getInsertDirection()) return fullAccessArray
            if (side == holder.getExtractDirection()) return fullAccessArray
            return IntArray(0)
        }

        override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            if (size() > slot) return false
            if (dir == null) return false
            return (holder.getInsertDirection() == dir)
        }

        override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
            if (size() > slot) return false
            if (dir == null) return false
            return (holder.getExtractDirection() == dir)
        }

        override fun markDirty() {
            holder.markDirty()
        }
    }
}