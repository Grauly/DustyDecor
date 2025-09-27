package grauly.dustydecor.item

import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.ActionResult

class BulkVoidGoopItem(settings: Settings?) : Item(settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val pos = context.blockPos.offset(context.side).toCenterPos()
        context.world.addParticleClient(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0)
        return ActionResult.SUCCESS
    }
}