package grauly.dustydecor.component

import grauly.dustydecor.ModComponentTypes
import grauly.dustydecor.ModItems
import grauly.dustydecor.packet.UpdateBulkGoopSizeC2SPacket
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ItemSlotMouseAction
import net.minecraft.client.ScrollWheelHandler
import net.minecraft.world.item.ItemStack
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.ClickType
import kotlin.math.sign

class BulkGoopSizeTooltipSubmenueHandler(
    val minecraftClient: Minecraft
) : ItemSlotMouseAction {
    private val scroller: ScrollWheelHandler = ScrollWheelHandler()
    override fun matches(slot: Slot): Boolean = slot.item.`is`(ModItems.BULK_VOID_GOOP)

    override fun onMouseScrolled(
        horizontal: Double,
        vertical: Double,
        slotId: Int,
        item: ItemStack
    ): Boolean {
        val vec = scroller.onMouseScroll(horizontal, vertical)
        val scrollProgress = if (vec.y == 0) -vec.x else -vec.y
        if (scrollProgress == 0) return true
        if (!item.has(ModComponentTypes.VOID_GOOP_SIZE)) return true
        val multiplier = if (minecraftClient.hasShiftDown()) 10 else 1
        val currentGoop = item.get(ModComponentTypes.VOID_GOOP_SIZE)!!.size
        var newGoop = (currentGoop + scrollProgress.sign * multiplier)
        if (newGoop > BulkGoopSizeComponent.MAX_SIZE) newGoop -= BulkGoopSizeComponent.MAX_SIZE
        if (newGoop <= 0) newGoop += BulkGoopSizeComponent.MAX_SIZE
        if (currentGoop != newGoop) {
            val actualSlot =
                if (minecraftClient.player!!.isCreative) minecraftClient.player!!.inventory.findSlotMatchingItem(item) else slotId
            sendPacket(item, actualSlot, newGoop)
        }
        return true
    }

    private fun sendPacket(item: ItemStack, slotId: Int, newGoop: Int) {
        if (minecraftClient.connection == null) return
        if (!item.`is`(ModItems.BULK_VOID_GOOP)) return
        if (newGoop > BulkGoopSizeComponent.MAX_SIZE) return
        ClientPlayNetworking.send(UpdateBulkGoopSizeC2SPacket(slotId, newGoop))
    }

    override fun onStopHovering(slot: Slot) {
        //[Space intentionally left blank]
    }

    override fun onSlotClicked(
        slot: Slot,
        actionType: ClickType
    ) {
        //[Space intentionally left blank]
    }
}