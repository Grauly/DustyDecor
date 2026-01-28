package grauly.dustydecor.entity

import net.minecraft.client.render.Frustum
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.entity.Entity

class EmptyEntityRenderer<T : Entity>(context: EntityRendererFactory.Context) :
    EntityRenderer<T, EntityRenderState>(context) {
    private val state = EntityRenderState()

    override fun createRenderState(): EntityRenderState {
        return state
    }

    override fun shouldRender(
        entity: T?,
        frustum: Frustum?,
        x: Double,
        y: Double,
        z: Double
    ): Boolean = false
}