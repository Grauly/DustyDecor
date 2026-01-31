package grauly.dustydecor.screen

import grauly.dustydecor.DustyDecorMod
import grauly.dustydecor.blockentity.vac_station.CopperGolemMode
import grauly.dustydecor.blockentity.vac_station.RedstoneEmissionMode
import grauly.dustydecor.blockentity.vac_station.SendMode
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.GOLEM_MODE
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.REDSTONE_MODE
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity.Companion.SEND_MODE
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.ContainerListener
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import kotlin.math.floor

abstract class VacPipeStationScreenHandler<T : AbstractContainerMenu> private constructor(
    type: MenuType<T>,
    syncId: Int
) : AbstractContainerMenu(type, syncId), ContainerListener {
    private lateinit var playerInventory: Inventory
    private lateinit var inventory: Container
    private lateinit var context: ContainerLevelAccess
    private lateinit var propertyDelegate: ContainerData

    constructor(
        type: MenuType<T>,
        syncId: Int,
        playerInventory: Inventory,
        inventory: Container,
        context: ContainerLevelAccess,
        propertyDelegate: ContainerData
    ) : this(type, syncId) {
        this.playerInventory = playerInventory
        this.inventory = inventory
        this.context = context
        this.propertyDelegate = propertyDelegate
        checkContainerSize(this.inventory, 3)
        inventory.startOpen(this.playerInventory.player)
        init()
        addSlotListener(this)
    }

    constructor(type: MenuType<T>, syncId: Int, playerInventory: Inventory) : this(type, syncId) {
        this.playerInventory = playerInventory
        this.inventory = SimpleContainer(3)
        this.context = ContainerLevelAccess.NULL
        this.propertyDelegate = SimpleContainerData(4)
        init()
    }

    open fun init() {
        addVariantSlots(this.inventory)
        addStandardInventorySlots(this.playerInventory, 8, 107)
        addDataSlots(propertyDelegate)
    }

    override fun clickMenuButton(player: Player?, id: Int): Boolean {
        val category = floor(id / 10.0).toInt()
        val value = id - category * 10
        propertyDelegate.set(category, value)
        broadcastChanges()
        return true
    }

    abstract fun addVariantSlots(inventory: Container)

    override fun quickMoveStack(
        player: Player?,
        slotIndex: Int
    ): ItemStack? {
        val fromSlot: Slot = slots[slotIndex]
        if (!fromSlot.hasItem()) return ItemStack.EMPTY
        val movingStack = fromSlot.item
        val originalStack = movingStack.copy()
        val fromInvToPlayer = slotIndex < inventory.containerSize
        val managedFit = moveItemStackTo(
            movingStack,
            if (fromInvToPlayer) inventory.containerSize else 0,
            if (fromInvToPlayer) slots.size else inventory.containerSize,
            fromInvToPlayer
        )
        if (!managedFit) return ItemStack.EMPTY
        if (movingStack.isEmpty) fromSlot.setByPlayer(ItemStack.EMPTY) else fromSlot.setChanged()
        return originalStack
    }

    override fun stillValid(player: Player?): Boolean =
        inventory.stillValid(player)

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
        broadcastChanges()
    }

    fun setSendingMode(sendMode: SendMode) {
        propertyDelegate.set(SEND_MODE, sendMode.ordinal)
        broadcastChanges()
    }

    fun setRedstoneMode(redstoneEmissionMode: RedstoneEmissionMode) {
        propertyDelegate.set(REDSTONE_MODE, redstoneEmissionMode.ordinal)
        broadcastChanges()
    }

    override fun dataChanged(
        handler: AbstractContainerMenu?,
        property: Int,
        value: Int
    ) {
        broadcastFullState()
        broadcastChanges()
    }

    override fun slotChanged(
        handler: AbstractContainerMenu?,
        slotId: Int,
        stack: ItemStack?
    ) {
        //[Space intentionally left blank]
    }
}