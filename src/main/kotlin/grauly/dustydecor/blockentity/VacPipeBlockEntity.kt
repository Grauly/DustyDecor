package grauly.dustydecor.blockentity

import com.mojang.serialization.Codec
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.vacpipe.AbConnectableBlock
import grauly.dustydecor.block.vacpipe.ConnectionState
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.RegistryWrapper
import net.minecraft.storage.NbtWriteView
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.ErrorReporter
import net.minecraft.util.HeldItemContext
import net.minecraft.util.ItemScatterer
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
    var insertHash = 0
    @Environment(EnvType.CLIENT)
    var lastInsertHash: Int = 0
    @Environment(EnvType.CLIENT)
    var ticksSinceLastChange: Int = 0

    fun tick(world: World, pos: BlockPos, state: BlockState) {
        if (world.isClient) {
            ticksSinceLastChange++
            return //need to return at end
        }
        handleItemMoving(world, pos, state)
    }

    private fun handleItemMoving(world: World, pos: BlockPos, state: BlockState) {
        if (storage.isResourceBlank) return
        if (isWaitingToMoveItems(world)) return
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

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener?>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup?): NbtCompound? {
        ErrorReporter.Logging(this.reporterContext, DustyDecorMod.logger).use {
            val view = NbtWriteView.create(it, registries)
            writeData(view)
            return view.nbt
        }
    }

    fun notifyInsert(world: World) {
        lastInsertTime = world.time
        insertHash = world.random.nextInt()
    }

    fun isWaitingToMoveItems(world: World): Boolean {
        return lastInsertTime == world.time
    }

    fun getItemsForScattering(): DefaultedList<ItemStack> {
        val list = DefaultedList.ofSize<ItemStack>(1)
        list.add(storage.variant.toStack(storage.amount.toInt()))
        return list
    }

    override fun onBlockReplaced(pos: BlockPos?, oldState: BlockState?) {
        ItemScatterer.spawn(world, pos, getItemsForScattering())
    }

    override fun markDirty() {
        super.markDirty()
        if (world == null) return
        world!!.updateListeners(pos, cachedState, cachedState, Block.NOTIFY_ALL)
    }

    fun getInsertDirection(): Direction? = cachedState.get(AbConnectableBlock.connections[0]).direction
    fun getExtractDirection(): Direction? = cachedState.get(AbConnectableBlock.connections[1]).direction

    override fun readData(view: ReadView) {
        storage.variant = view.read("itemVariant", ItemVariant.CODEC).orElse(ItemVariant.blank())
        storage.amount = view.read("amount", Codec.LONG).orElse(0L)
        lastInsertTime = view.read("lastInsertTime", Codec.LONG).orElse(0L)
        insertHash = view.read("insertHash", Codec.INT).orElse(0)
        super.readData(view)
    }

    override fun writeData(view: WriteView) {
        view.put("itemVariant", ItemVariant.CODEC, storage.variant)
        view.put("amount", Codec.LONG, storage.amount)
        view.put("lastInsertTime", Codec.LONG, lastInsertTime)
        view.put("insertHash", Codec.INT, insertHash)
        super.writeData(view)
    }

    override fun getEntityWorld(): World? = world

    override fun getEntityPos(): Vec3d? = pos.toCenterPos()

    override fun getBodyYaw(): Float = 0f

}