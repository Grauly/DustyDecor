package grauly.dustydecor.entity

import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.world.entity.Entity

class EmptyEntityRenderer<T : Entity>(context: EntityRendererProvider.Context) :
    EntityRenderer<T, EntityRenderState>(context) {
    private val state = EntityRenderState()

    override fun createRenderState(): EntityRenderState {
        return state
    }

    override fun shouldRender(
        entity: T,
        frustum: Frustum,
        x: Double,
        y: Double,
        z: Double
    ): Boolean = false
}