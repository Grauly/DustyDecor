package grauly.dustydecor.blockentity

import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.inventory.SidedSelfCompactingInventory
import grauly.dustydecor.screen.VacPipeStationScreenHandler
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
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
) : BlockEntity(ModBlockEntityTypes.VAC_PIPE_STATION_ENTITY, pos, state), SidedSelfCompactingInventory, NamedScreenHandlerFactory {
    override val items: DefaultedList<ItemStack> = DefaultedList.ofSize(3, ItemStack.EMPTY)
    override fun insertDirections(): Set<Direction> = setOf(Direction.UP)
    override fun extractDirections(): Set<Direction> = setOf(Direction.UP)
    override fun canPlayerUse(player: PlayerEntity?): Boolean = true

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?): ScreenHandler {
        return VacPipeStationScreenHandler(syncId, playerInventory, this)
    }

    override fun getDisplayName(): Text {
        return Text.translatable(cachedState.block.translationKey)
    }

    override fun readData(view: ReadView) {
        super.readData(view)
        Inventories.readData(view, items)
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, items)
    }
}