package grauly.dustydecor.component

import grauly.dustydecor.ModComponentTypes
import grauly.dustydecor.ModItems
import grauly.dustydecor.packet.UpdateBulkGoopSizeC2SPacket
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler
import net.minecraft.client.input.Scroller
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import kotlin.math.sign

class BulkGoopSizeTooltipSubmenueHandler(
    val minecraftClient: MinecraftClient
) : TooltipSubmenuHandler {
    private val scroller: Scroller = Scroller()
    override fun isApplicableTo(slot: Slot): Boolean = slot.stack.isOf(ModItems.BULK_VOID_GOOP)

    override fun onScroll(
        horizontal: Double,
        vertical: Double,
        slotId: Int,
        item: ItemStack
    ): Boolean {
        val vec = scroller.update(horizontal, vertical)
        val scrollProgress = if (vec.y == 0) -vec.x else -vec.y
        if (scrollProgress == 0) return true
        if (!item.contains(ModComponentTypes.VOID_GOOP_SIZE)) return true
        val multiplier = if (minecraftClient.isShiftPressed) 10 else 1
        val currentGoop = item.get(ModComponentTypes.VOID_GOOP_SIZE)!!.size
        var newGoop = (currentGoop + scrollProgress.sign * multiplier)
        if (newGoop > BulkGoopSizeComponent.MAX_SIZE) newGoop -= BulkGoopSizeComponent.MAX_SIZE
        if (newGoop <= 0) newGoop += BulkGoopSizeComponent.MAX_SIZE
        if (currentGoop != newGoop) {
            val actualSlot =
                if (minecraftClient.player!!.isCreative) minecraftClient.player!!.inventory.getSlotWithStack(item) else slotId
            sendPacket(item, actualSlot, newGoop)
        }
        return true
    }

    private fun sendPacket(item: ItemStack, slotId: Int, newGoop: Int) {
        if (minecraftClient.networkHandler == null) return
        if (!item.isOf(ModItems.BULK_VOID_GOOP)) return
        if (newGoop > BulkGoopSizeComponent.MAX_SIZE) return
        ClientPlayNetworking.send(UpdateBulkGoopSizeC2SPacket(slotId, newGoop))
    }

    override fun reset(slot: Slot) {
        //[Space intentionally left blank]
    }

    override fun onMouseClick(
        slot: Slot,
        actionType: SlotActionType
    ) {
        //[Space intentionally left blank]
    }
}