package grauly.dustydecor.blockentity

import com.mojang.serialization.Codec
import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.AbConnectableBlock
import grauly.dustydecor.block.ConnectionState
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantItemStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import kotlin.math.min

class VacPipeBlockEntity(
    pos: BlockPos?,
    state: BlockState?,
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_ENTITY, pos, state) {
    val storage: SingleVariantStorage<ItemVariant> = object : SingleVariantStorage<ItemVariant>() {
        override fun getCapacity(itemVariant: ItemVariant): Long = itemVariant.toStack().maxCount.toLong()
        override fun getBlankVariant(): ItemVariant = ItemVariant.blank()
        override fun onFinalCommit() {
            super.onFinalCommit()
            markDirty()
        }
    }

    fun getItemsForScattering(): DefaultedList<ItemStack> {
        val list = DefaultedList.ofSize<ItemStack>(1)
        list.add(storage.variant.toStack(storage.amount.toInt()))
        return list
    }

    fun tick(world: World, pos: BlockPos, state: BlockState) {
        if (world.isClient) return
        if (storage.isResourceBlank) return
        val followDirection = state.get(AbConnectableBlock.connections[1])
        if (followDirection == ConnectionState.NONE) return
        val targetBe = world.getBlockEntity(pos.offset(followDirection.direction))
        if (targetBe !is VacPipeBlockEntity && targetBe !is VacPipeStationBlockEntity) return
        val targetStorage =
            ItemStorage.SIDED.find(world, pos.offset(followDirection.direction), followDirection.direction?.opposite)
                ?: return
        StorageUtil.move(storage, targetStorage, { true }, 1, null)
    }

    fun getInsertDirection(): Direction? = cachedState.get(AbConnectableBlock.connections[0]).direction
    fun getExtractDirection(): Direction? = cachedState.get(AbConnectableBlock.connections[1]).direction

    override fun readData(view: ReadView) {
        storage.variant = view.read("itemVariant", ItemVariant.CODEC).orElse(ItemVariant.blank())
        storage.amount = view.read("amount", Codec.LONG).orElse(0L)
        super.readData(view)
    }

    override fun writeData(view: WriteView) {
        view.put("itemVariant", ItemVariant.CODEC, storage.variant)
        view.put("amount", Codec.LONG, storage.amount)
        super.writeData(view)
    }

}