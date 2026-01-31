package grauly.dustydecor

import grauly.dustydecor.entity.EmptyEntityRenderer
import grauly.dustydecor.entity.SeatEntity
import net.minecraft.client.renderer.entity.EntityRenderers

object ModEntityRenderers {

    fun init() {
        EntityRenderers.register(
            ModEntities.SEAT_ENTITY,
            { context -> EmptyEntityRenderer<SeatEntity>(context) }
        )
    }
}