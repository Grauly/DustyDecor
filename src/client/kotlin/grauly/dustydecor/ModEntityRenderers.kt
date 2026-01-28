package grauly.dustydecor

import grauly.dustydecor.entity.EmptyEntityRenderer
import grauly.dustydecor.entity.SeatEntity
import net.minecraft.client.render.entity.EntityRendererFactories

object ModEntityRenderers {

    fun init() {
        EntityRendererFactories.register(
            ModEntities.SEAT_ENTITY,
            { context -> EmptyEntityRenderer<SeatEntity>(context) }
        )
    }
}