package grauly.dustydecor.blockentity

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState
import net.minecraft.client.render.item.ItemRenderState

class VacPipeBlockEntityRendererContext(
    var itemRenderState: ItemRenderState = ItemRenderState()
) : BlockEntityRenderState()