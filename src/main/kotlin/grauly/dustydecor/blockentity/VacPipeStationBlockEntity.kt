package grauly.dustydecor.blockentity

import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.screen.VacPipeStationScreenHandler
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class VacPipeStationBlockEntity(
    pos: BlockPos?,
    state: BlockState?
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_STATION_ENTITY, pos, state), NamedScreenHandlerFactory {
    private val inventory = object : SimpleInventory(3) {}
    val storage: InventoryStorage = InventoryStorage.of(inventory, Direction.UP)

    fun getItemForScattering(): DefaultedList<ItemStack> {
        return inventory.heldStacks
    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?): ScreenHandler {
        return VacPipeStationScreenHandler(syncId, playerInventory, inventory)
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