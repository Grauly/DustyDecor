package grauly.dustydecor.blockentity

import com.mojang.serialization.Codec
import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.AbConnectableBlock
import grauly.dustydecor.block.ConnectionState
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.HeldItemContext
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class VacPipeBlockEntity(
    pos: BlockPos?,
    state: BlockState?,
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_ENTITY, pos, state), HeldItemContext {
    val storage: SingleVariantStorage<ItemVariant> = object : SingleVariantStorage<ItemVariant>() {
        override fun getCapacity(itemVariant: ItemVariant): Long = itemVariant.toStack().maxCount.toLong()
        override fun getBlankVariant(): ItemVariant = ItemVariant.blank()
        override fun onFinalCommit() {
            super.onFinalCommit()
            markDirty()
        }
    }
    private var lastInsertTime = 0L

    fun getItemsForScattering(): DefaultedList<ItemStack> {
        val list = DefaultedList.ofSize<ItemStack>(1)
        list.add(storage.variant.toStack(storage.amount.toInt()))
        return list
    }

    fun notifyInsert(world: World) {
        lastInsertTime = world.time
    }

    fun tick(world: World, pos: BlockPos, state: BlockState) {
        if (world.isClient) return
        handleItemMoving(world, pos, state)
    }

    private fun handleItemMoving(world: World, pos: BlockPos, state: BlockState) {
        if (storage.isResourceBlank) return
        if (lastInsertTime == world.time) return
        val followDirection = state.get(AbConnectableBlock.connections[1])
        if (followDirection == ConnectionState.NONE) return
        val targetBe = world.getBlockEntity(pos.offset(followDirection.direction))
        if (targetBe !is VacPipeBlockEntity && targetBe !is VacPipeStationBlockEntity) return
        val targetStorage =
            ItemStorage.SIDED.find(world, pos.offset(followDirection.direction), followDirection.direction?.opposite)
                ?: return
        val movedAmount = StorageUtil.move(storage, targetStorage, { true }, 1, null)
        if (movedAmount > 0 && targetBe is VacPipeBlockEntity) targetBe.notifyInsert(world)
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

    override fun getEntityWorld(): World? = world

    override fun getEntityPos(): Vec3d? = pos.toCenterPos()

    override fun getBodyYaw(): Float = 0f

}