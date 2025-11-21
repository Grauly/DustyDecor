package grauly.dustydecor.screen

import grauly.dustydecor.blockentity.vac_station.CopperGolemMode
import grauly.dustydecor.blockentity.vac_station.EnumButtonIdHolder
import grauly.dustydecor.blockentity.vac_station.RedstoneEmissionMode
import grauly.dustydecor.blockentity.vac_station.SendMode
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

abstract class VacPipeStationScreenHandler<T: ScreenHandler>(
    type: ScreenHandlerType<T>,
    syncId: Int,
    playerInventory: PlayerInventory,
    private val inventory: Inventory,
    val pos: BlockPos?
): ScreenHandler(type, syncId) {

    init {
        checkSize(inventory, 3)
        inventory.onOpen(playerInventory.player)
        addVariantSlots(inventory)
        populateInventorySlots(playerInventory, this::addSlot)
    }

    abstract fun addVariantSlots(inventory: Inventory)

    override fun onButtonClick(player: PlayerEntity, id: Int): Boolean {
        pos ?: return super.onButtonClick(player, id)
        if(player.entityWorld.isClient) return super.onButtonClick(player, id)
        val station = (player.entityWorld as ServerWorld).getBlockEntity(pos)
        if (station !is VacPipeStationBlockEntity) return super.onButtonClick(player, id)
        val selectedOption = optionsList.find { o -> o.getId() == id } ?: return onNotDefaultOptionButtonClick(player, id, pos)
        when (selectedOption) {
            is RedstoneEmissionMode -> {
                station.setRedstoneMode(selectedOption)
            }

            is CopperGolemMode -> {
                station.setGolemMode(selectedOption)
            }

            is SendMode -> {
                station.setSendMode(selectedOption)
            }
        }
        return super.onButtonClick(player, id)
    }

    abstract fun onNotDefaultOptionButtonClick(player: PlayerEntity, id: Int, pos: BlockPos): Boolean

    protected fun populateInventorySlots(playerInventory: Inventory, slotAdder: (Slot) -> Slot) {
        for (y in 0..2) {
            for (x in 0..8) {
                slotAdder.invoke(Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 107 + y * 18))
            }
        }

        for (i in 0..8) {
            slotAdder.invoke(Slot(playerInventory, i, 8 + i * 18, 165))
        }
    }

    override fun quickMove(
        player: PlayerEntity?,
        slotIndex: Int
    ): ItemStack? {
        val fromSlot: Slot = slots[slotIndex]
        if (!fromSlot.hasStack()) return ItemStack.EMPTY
        val movingStack = fromSlot.stack
        val originalStack = movingStack.copy()
        val fromInvToPlayer = slotIndex < inventory.size()
        val managedFit = insertItem(
            movingStack,
            if (fromInvToPlayer) inventory.size() else 0,
            if (fromInvToPlayer) slots.size else inventory.size(),
            fromInvToPlayer
        )
        if (!managedFit) return ItemStack.EMPTY
        if (movingStack.isEmpty) fromSlot.stack = ItemStack.EMPTY else fromSlot.markDirty()
        return originalStack
    }

    override fun canUse(player: PlayerEntity?): Boolean =
        inventory.canPlayerUse(player)

    companion object {
        private val optionsList: List<EnumButtonIdHolder> = run {
            val mutableList = mutableListOf<EnumButtonIdHolder>()
            mutableList.addAll(CopperGolemMode.entries)
            mutableList.addAll(RedstoneEmissionMode.entries)
            mutableList.addAll(SendMode.entries)
            mutableList
        }
    }
}