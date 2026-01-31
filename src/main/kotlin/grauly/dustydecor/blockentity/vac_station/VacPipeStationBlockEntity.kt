package grauly.dustydecor.blockentity.vac_station

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.vacpipe.VacPipeStationBlock
import grauly.dustydecor.block.vacpipe.VacPipeStationBlock.Companion.SENDING
import grauly.dustydecor.screen.VacPipeReceiveStationScreenHandler
import grauly.dustydecor.screen.VacPipeSendStationScreenHandler
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.core.HolderLookup
import net.minecraft.world.MenuProvider
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.level.storage.TagValueOutput
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.network.chat.Component
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.Containers
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import kotlin.use

class VacPipeStationBlockEntity(
    pos: BlockPos?,
    state: BlockState?
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_STATION_ENTITY, pos, state), MenuProvider, ItemOwner {
    private val inventory = object : SimpleContainer(INVENTORY_SIZE) {
        override fun setChanged() {
            markDirtyDelegate()
        }
    }
    val storage: InventoryStorage = InventoryStorage.of(inventory, Direction.UP)

    val propertyDelegate = ModeDelegate()

    fun markDirtyDelegate() {
        setChanged()
    }

    fun getItemsForScattering(): NonNullList<ItemStack> {
        return inventory.items
    }

    override fun preRemoveSideEffects(pos: BlockPos?, oldState: BlockState?) {
        Containers.dropContents(level, pos, getItemsForScattering())
    }

    override fun createMenu(syncId: Int, playerInventory: Inventory, player: Player?): AbstractContainerMenu {
        val handlerConstructor: (Int, Inventory, Container, ContainerLevelAccess, ContainerData) -> AbstractContainerMenu =
            if (blockState.getValue(SENDING)) {
                ::VacPipeSendStationScreenHandler
            } else {
                ::VacPipeReceiveStationScreenHandler
            }
        return handlerConstructor.invoke(syncId, playerInventory, inventory, ContainerLevelAccess.create(
            level,
            worldPosition
        ), propertyDelegate)
    }

    override fun getDisplayName(): Component {
        return Component.translatable(blockState.block.descriptionId)
    }

    override fun setChanged() {
        super.setChanged()
        if (level == null) return
        level!!.sendBlockUpdated(worldPosition, blockState, blockState, Block.UPDATE_ALL)
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

    override fun loadAdditional(view: ValueInput) {
        super.loadAdditional(view)
        inventory.items.clear()
        ContainerHelper.loadAllItems(view, inventory.items)
        propertyDelegate.setGolemMode(view.read(GOLEM_MODE_KEY, CopperGolemMode.CODEC).orElseGet { CopperGolemMode.INTERACT })
        propertyDelegate.setRedstoneMode(view.read(REDSTONE_MODE_KEY, RedstoneEmissionMode.CODEC).orElseGet { RedstoneEmissionMode.ON_RECEIVE })
        propertyDelegate.setSendingMode(view.read(SEND_MODE_KEY, SendMode.CODEC).orElseGet { SendMode.MANUAL })
    }

    override fun saveAdditional(view: ValueOutput) {
        super.saveAdditional(view)
        ContainerHelper.saveAllItems(view, inventory.items)
        view.store(GOLEM_MODE_KEY, CopperGolemMode.CODEC, propertyDelegate.getGolemMode())
        view.store(REDSTONE_MODE_KEY, RedstoneEmissionMode.CODEC, propertyDelegate.getRedstoneMode())
        view.store(SEND_MODE_KEY, SendMode.CODEC, propertyDelegate.getSendingMode())
    }

    override fun level(): Level? = level

    override fun position(): Vec3? = worldPosition.center

    override fun getVisualRotationYInDegrees(): Float = 0f

    companion object {
         const val GOLEM_MODE_KEY = "golemMode"
         const val REDSTONE_MODE_KEY = "redstoneMode"
         const val SEND_MODE_KEY = "sendingMode"

         const val GOLEM_MODE = 0
         const val REDSTONE_MODE = 1
         const val SEND_MODE = 2

        const val INVENTORY_SIZE = 3
    }

    class ModeDelegate() : ContainerData {
        val data = IntArray(3)

        override fun get(index: Int): Int {
            return when (index) {
                0,1,2 -> data[index]
                else -> -1
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0,1,2 -> data[index] = value
            }
        }

        fun getGolemMode(): CopperGolemMode {
            return CopperGolemMode.entries[get(GOLEM_MODE)]
        }

        fun getRedstoneMode(): RedstoneEmissionMode {
            return RedstoneEmissionMode.entries[get(REDSTONE_MODE)]
        }

        fun getSendingMode(): SendMode {
            return SendMode.entries[get(SEND_MODE)]
        }

        fun setGolemMode(golemMode: CopperGolemMode) {
            set(GOLEM_MODE, golemMode.ordinal)
        }

        fun setRedstoneMode(redstoneMode: RedstoneEmissionMode) {
            set(REDSTONE_MODE, redstoneMode.ordinal)
        }

        fun setSendingMode(sendingMode: SendMode) {
            set(SEND_MODE, sendingMode.ordinal)
        }

        override fun getCount(): Int = 4
    }
}