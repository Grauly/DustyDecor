package grauly.dustydecor.blockentity.vac_station

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
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.ItemScatterer
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class VacPipeStationBlockEntity(
    pos: BlockPos?,
    state: BlockState?
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_STATION_ENTITY, pos, state), NamedScreenHandlerFactory {
    private val inventory = object : SimpleInventory(3) {}
    val storage: InventoryStorage = InventoryStorage.of(inventory, Direction.UP)

    private var golemMode: CopperGolemMode = CopperGolemMode.INTERACT
    private var redstoneMode: RedstoneEmissionMode = RedstoneEmissionMode.ON_ARRIVAL
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

        override fun size(): Int = 3
    }

    fun getItemsForScattering(): DefaultedList<ItemStack> {
        return inventory.heldStacks
    }

    override fun onBlockReplaced(pos: BlockPos?, oldState: BlockState?) {
        ItemScatterer.spawn(world, pos, getItemsForScattering())
    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?): ScreenHandler {
        return if (cachedState.get(VacPipeStationBlock.Companion.SENDING)) {
            VacPipeSendStationScreenHandler(syncId, playerInventory, inventory, pos)
        } else {
            VacPipeReceiveStationScreenHandler(syncId, playerInventory, inventory, pos)
        }
    }

    override fun getDisplayName(): Text {
        return Text.translatable(cachedState.block.translationKey)
    }

    override fun readData(view: ReadView) {
        super.readData(view)
        Inventories.readData(view, inventory.heldStacks)
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, inventory.heldStacks)
    }
}