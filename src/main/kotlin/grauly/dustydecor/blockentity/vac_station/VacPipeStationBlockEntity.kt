package grauly.dustydecor.blockentity.vac_station

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.vacpipe.VacPipeStationBlock
import grauly.dustydecor.block.vacpipe.VacPipeStationBlock.Companion.SENDING
import grauly.dustydecor.screen.VacPipeReceiveStationScreenHandler
import grauly.dustydecor.screen.VacPipeSendStationScreenHandler
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.RegistryWrapper
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.storage.NbtWriteView
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.ErrorReporter
import net.minecraft.util.ItemScatterer
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import kotlin.use

class VacPipeStationBlockEntity(
    pos: BlockPos?,
    state: BlockState?
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_STATION_ENTITY, pos, state), NamedScreenHandlerFactory {
    private val inventory = object : SimpleInventory(3) {}
    val storage: InventoryStorage = InventoryStorage.of(inventory, Direction.UP)

    val propertyDelegate = ModeDelegate()

    fun getItemsForScattering(): DefaultedList<ItemStack> {
        return inventory.heldStacks
    }

    override fun onBlockReplaced(pos: BlockPos?, oldState: BlockState?) {
        ItemScatterer.spawn(world, pos, getItemsForScattering())
    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?): ScreenHandler {
        val handlerConstructor: (Int, PlayerInventory, Inventory, ScreenHandlerContext, PropertyDelegate) -> ScreenHandler =
            if (cachedState.get(SENDING)) {
                ::VacPipeSendStationScreenHandler
            } else {
                ::VacPipeReceiveStationScreenHandler
            }
        return handlerConstructor.invoke(syncId, playerInventory, inventory, ScreenHandlerContext.create(world, pos), propertyDelegate)
    }

    override fun getDisplayName(): Text {
        return Text.translatable(cachedState.block.translationKey)
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

    override fun readData(view: ReadView) {
        super.readData(view)
        Inventories.readData(view, inventory.heldStacks)
        propertyDelegate.setGolemMode(view.read(GOLEM_MODE_KEY, CopperGolemMode.CODEC).orElseGet { CopperGolemMode.INTERACT })
        propertyDelegate.setRedstoneMode(view.read(REDSTONE_MODE_KEY, RedstoneEmissionMode.CODEC).orElseGet { RedstoneEmissionMode.ON_RECEIVE })
        propertyDelegate.setSendingMode(view.read(SEND_MODE_KEY, SendMode.CODEC).orElseGet { SendMode.MANUAL })
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inventory.heldStacks)
        view.put(GOLEM_MODE_KEY, CopperGolemMode.CODEC, propertyDelegate.getGolemMode())
        view.put(REDSTONE_MODE_KEY, RedstoneEmissionMode.CODEC, propertyDelegate.getRedstoneMode())
        view.put(SEND_MODE_KEY, SendMode.CODEC, propertyDelegate.getSendingMode())
    }

    companion object {
         const val GOLEM_MODE_KEY = "golemMode"
         const val REDSTONE_MODE_KEY = "redstoneMode"
         const val SEND_MODE_KEY = "sendingMode"

         const val GOLEM_MODE = 0
         const val REDSTONE_MODE = 1
         const val SEND_MODE = 2
    }

    class ModeDelegate() : PropertyDelegate {
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

        override fun size(): Int = 4
    }
}