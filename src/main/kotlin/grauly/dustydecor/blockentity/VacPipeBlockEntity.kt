package grauly.dustydecor.blockentity

import com.mojang.serialization.Codec
import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.vacpipe.AbConnectableBlock
import grauly.dustydecor.block.vacpipe.ConnectionState
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.storage.TagValueOutput
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.Containers
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level

class VacPipeBlockEntity(
    pos: BlockPos?,
    state: BlockState?,
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_ENTITY, pos, state), ItemOwner {
    val storage: SingleVariantStorage<ItemVariant> = object : SingleVariantStorage<ItemVariant>() {
        override fun getCapacity(itemVariant: ItemVariant): Long = itemVariant.toStack().maxStackSize.toLong()
        override fun getBlankVariant(): ItemVariant = ItemVariant.blank()
        override fun onFinalCommit() {
            super.onFinalCommit()
            setChanged()
        }
    }
    private var lastInsertTime = 0L
    var insertHash = 0
    @Environment(EnvType.CLIENT)
    var lastInsertHash: Int = 0
    @Environment(EnvType.CLIENT)
    var ticksSinceLastChange: Int = 0

    fun tick(world: Level, pos: BlockPos, state: BlockState) {
        if (world.isClientSide) {
            ticksSinceLastChange++
            return //need to return at end
        }
        handleItemMoving(world, pos, state)
    }

    private fun handleItemMoving(world: Level, pos: BlockPos, state: BlockState) {
        if (storage.isResourceBlank) return
        if (isWaitingToMoveItems(world)) return
        val followDirection = state.getValue(AbConnectableBlock.connections[1])
        if (followDirection == ConnectionState.NONE) return
        val targetBe = world.getBlockEntity(pos.relative(followDirection.direction))
        if (targetBe !is VacPipeBlockEntity && targetBe !is VacPipeStationBlockEntity) return
        val targetStorage =
            ItemStorage.SIDED.find(world, pos.relative(followDirection.direction), followDirection.direction?.opposite)
                ?: return
        val movedAmount = StorageUtil.move(storage, targetStorage, { true }, 1, null)
        if (movedAmount > 0 && targetBe is VacPipeBlockEntity) targetBe.notifyInsert(world)
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener?>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider?): CompoundTag? {
        ProblemReporter.ScopedCollector(this.problemPath(), DustyDecorMod.logger).use {
            val view = TagValueOutput.createWithContext(it, registries)
            saveAdditional(view)
            return view.buildResult()
        }
    }

    fun notifyInsert(world: Level) {
        lastInsertTime = world.gameTime
        insertHash = world.random.nextInt()
    }

    fun isWaitingToMoveItems(world: Level): Boolean {
        return lastInsertTime == world.gameTime
    }

    fun getItemsForScattering(): NonNullList<ItemStack> {
        val list = NonNullList.createWithCapacity<ItemStack>(1)
        list.add(storage.variant.toStack(storage.amount.toInt()))
        return list
    }

    override fun preRemoveSideEffects(pos: BlockPos?, oldState: BlockState?) {
        Containers.dropContents(level, pos, getItemsForScattering())
    }

    override fun setChanged() {
        super.setChanged()
        if (level == null) return
        level!!.sendBlockUpdated(worldPosition, blockState, blockState, Block.UPDATE_ALL)
    }

    fun getInsertDirection(): Direction? = blockState.getValue(AbConnectableBlock.connections[0]).direction
    fun getExtractDirection(): Direction? = blockState.getValue(AbConnectableBlock.connections[1]).direction

    override fun loadAdditional(view: ValueInput) {
        storage.variant = view.read("itemVariant", ItemVariant.CODEC).orElse(ItemVariant.blank())
        storage.amount = view.read("amount", Codec.LONG).orElse(0L)
        lastInsertTime = view.read("lastInsertTime", Codec.LONG).orElse(0L)
        insertHash = view.read("insertHash", Codec.INT).orElse(0)
        super.loadAdditional(view)
    }

    override fun saveAdditional(view: ValueOutput) {
        view.store("itemVariant", ItemVariant.CODEC, storage.variant)
        view.store("amount", Codec.LONG, storage.amount)
        view.store("lastInsertTime", Codec.LONG, lastInsertTime)
        view.store("insertHash", Codec.INT, insertHash)
        super.saveAdditional(view)
    }

    override fun level(): Level? = level

    override fun position(): Vec3? = worldPosition.center

    override fun getVisualRotationYInDegrees(): Float = 0f

}