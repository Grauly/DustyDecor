package grauly.dustydecor

import grauly.dustydecor.hud.VoidGoopOverlayRenderer
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.util.Identifier

object ModHudElements {
    fun init() {
        HudElementRegistry.attachElementAfter(
            VanillaHudElements.MISC_OVERLAYS,
            Identifier.of(DustyDecorMod.MODID, "void_goop_overlay"),
        ) {
            context, tick ->
            VoidGoopOverlayRenderer.render(context, tick)
        }
    }
}