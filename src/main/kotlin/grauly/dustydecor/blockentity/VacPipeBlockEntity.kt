package grauly.dustydecor.blockentity

import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.AbConnectableBlock
import grauly.dustydecor.block.ConnectionState
import grauly.dustydecor.inventory.SidedSelfCompactingInventory
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class VacPipeBlockEntity(
    pos: BlockPos?,
    state: BlockState?,
    override val items: DefaultedList<ItemStack> = DefaultedList.ofSize(1, ItemStack.EMPTY)
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_ENTITY, pos, state), SidedSelfCompactingInventory {

    fun tick(world: World, pos: BlockPos, state: BlockState) {
        if (world.isClient) return
        if (isEmpty) return
        val followDirection = state.get(AbConnectableBlock.connections[1])
        if (followDirection == ConnectionState.NONE) return
        val targetBe = world.getBlockEntity(pos.offset(followDirection.direction))
        if (targetBe !is VacPipeBlockEntity && targetBe !is VacPipeStationBlockEntity) return
        val ownStorage = InventoryStorage.of(this, followDirection.direction)
        val targetStorage = ItemStorage.SIDED.find(world, pos.offset(followDirection.direction), followDirection.direction?.opposite)
        if (ownStorage == null || targetStorage == null) return
        StorageUtil.move(ownStorage, targetStorage, { true }, 1, null)
    }

    override fun insertDirections(): Set<Direction> {
        val aDirection = cachedState.get(AbConnectableBlock.connections[0]).direction
        return if (aDirection != null) setOf(aDirection) else setOf()
    }

    override fun extractDirections(): Set<Direction> {
        val bDirection = cachedState.get(AbConnectableBlock.connections[1]).direction
        return if (bDirection != null) setOf(bDirection) else setOf()
    }

    override fun canPlayerUse(player: PlayerEntity?): Boolean = false

    override fun readData(view: ReadView?) {
        super.readData(view)
        Inventories.readData(view, items)
    }

    override fun writeData(view: WriteView?) {
        super.writeData(view)
        Inventories.writeData(view, items)
    }
}