package grauly.dustydecor.screen

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.blockentity.vac_station.CopperGolemMode
import grauly.dustydecor.blockentity.vac_station.RedstoneEmissionMode
import grauly.dustydecor.blockentity.vac_station.SendMode
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.GOLEM_MODE
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.REDSTONE_MODE
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.SEND_MODE
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot

abstract class VacPipeStationScreenHandler<T : ScreenHandler> private constructor(
    type: ScreenHandlerType<T>,
    syncId: Int
) : ScreenHandler(type, syncId) {
    private lateinit var playerInventory: PlayerInventory
    private lateinit var inventory: Inventory
    private lateinit var context: ScreenHandlerContext
    private lateinit var propertyDelegate: PropertyDelegate

    constructor(
        type: ScreenHandlerType<T>,
        syncId: Int,
        playerInventory: PlayerInventory,
        inventory: Inventory,
        context: ScreenHandlerContext,
        propertyDelegate: PropertyDelegate
    ) : this(type, syncId) {
        this.playerInventory = playerInventory
        this.inventory = inventory
        this.context = context
        this.propertyDelegate = propertyDelegate
        checkSize(this.inventory, 3)
        inventory.onOpen(this.playerInventory.player)
        init()
    }

    constructor(type: ScreenHandlerType<T>, syncId: Int, playerInventory: PlayerInventory) : this(type, syncId) {
        this.playerInventory = playerInventory
        this.inventory = SimpleInventory(3)
        this.context = ScreenHandlerContext.EMPTY
        this.propertyDelegate = ArrayPropertyDelegate(4)
        init()
    }

    open fun init() {
        addVariantSlots(this.inventory)
        addPlayerSlots(this.playerInventory, 8, 107)
        addProperties(propertyDelegate)
    }

    abstract fun addVariantSlots(inventory: Inventory)

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

    fun getGolemMode(): CopperGolemMode {
        return CopperGolemMode.entries[propertyDelegate.get(GOLEM_MODE)]
    }

    fun getSendingMode(): SendMode {
        return SendMode.entries[propertyDelegate.get(SEND_MODE)]
    }

    fun getRedstoneMode(): RedstoneEmissionMode {
        return RedstoneEmissionMode.entries[propertyDelegate.get(REDSTONE_MODE)]
    }

    fun setGolemMode(golemMode: CopperGolemMode) {
        propertyDelegate.set(GOLEM_MODE, golemMode.ordinal)
        DustyDecorMod.logger.info("[VacPipeStationScreenHandler] Sending updated golem mode: $golemMode, ${golemMode.ordinal}")
        sendContentUpdates()
    }

    fun setSendingMode(sendMode: SendMode) {
        propertyDelegate.set(SEND_MODE, sendMode.ordinal)
        DustyDecorMod.logger.info("[VacPipeStationScreenHandler] Sending updated sending mode: $sendMode, ${sendMode.ordinal}")
        sendContentUpdates()
    }

    fun setRedstoneMode(redstoneEmissionMode: RedstoneEmissionMode) {
        propertyDelegate.set(REDSTONE_MODE, redstoneEmissionMode.ordinal)
        DustyDecorMod.logger.info("[VacPipeStationScreenHandler] Sending updated redstone mode: $redstoneEmissionMode, ${redstoneEmissionMode.ordinal}")
        sendContentUpdates()
    }
}