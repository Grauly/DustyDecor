package grauly.dustydecor.blockentity.vac_station

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.vacpipe.VacPipeStationBlock
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

    private var golemMode: CopperGolemMode = CopperGolemMode.INTERACT
    private var redstoneMode: RedstoneEmissionMode = RedstoneEmissionMode.ON_RECEIVE
    private var sendMode: SendMode = SendMode.MANUAL

    val propertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int {
            return when (index) {
                0 -> golemMode.ordinal
                1 -> redstoneMode.ordinal
                2 -> sendMode.ordinal
                3 -> if (cachedState.get(VacPipeStationBlock.SENDING)) 1 else 0
                else -> -1
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> golemMode = CopperGolemMode.entries[index]
                1 -> redstoneMode = RedstoneEmissionMode.entries[index]
                2 -> sendMode = SendMode.entries[index]
                3 -> {
                    if (world?.isClient ?: true) return
                    world?.setBlockState(pos, cachedState.with(VacPipeStationBlock.SENDING, value == 1))
                }
            }
        }

        override fun size(): Int = 4
    }

    fun getItemsForScattering(): DefaultedList<ItemStack> {
        return inventory.heldStacks
    }

    override fun onBlockReplaced(pos: BlockPos?, oldState: BlockState?) {
        ItemScatterer.spawn(world, pos, getItemsForScattering())
    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?): ScreenHandler {
        val handlerConstructor: (Int, PlayerInventory, Inventory, ScreenHandlerContext, PropertyDelegate) -> ScreenHandler =
            if (cachedState.get(VacPipeStationBlock.Companion.SENDING)) {
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
        golemMode = view.read(GOLEM_MODE_KEY, CopperGolemMode.CODEC).orElseGet { CopperGolemMode.INTERACT }
        redstoneMode = view.read(REDSTONE_MODE_KEY, RedstoneEmissionMode.CODEC).orElseGet { RedstoneEmissionMode.ON_RECEIVE }
        sendMode = view.read(SEND_MODE_KEY, SendMode.CODEC).orElseGet { SendMode.MANUAL }
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inventory.heldStacks)
        view.put(GOLEM_MODE_KEY, CopperGolemMode.CODEC, golemMode)
        view.put(REDSTONE_MODE_KEY, RedstoneEmissionMode.CODEC, redstoneMode)
        view.put(SEND_MODE_KEY, SendMode.CODEC, sendMode)
    }

    companion object {
        private const val GOLEM_MODE_KEY = "golemMode"
        private const val REDSTONE_MODE_KEY = "redstoneMode"
        private const val SEND_MODE_KEY = "sendingMode"
    }
}