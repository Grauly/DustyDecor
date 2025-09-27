package grauly.dustydecor.mixin.client;

import grauly.dustydecor.component.BulkGoopSizeTooltipSubmenueHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenSubmenueHandlerAdder extends Screen {

    protected HandledScreenSubmenueHandlerAdder(Text title) {
        super(title);
    }

    @Shadow
    protected abstract void addTooltipSubmenuHandler(TooltipSubmenuHandler handler);

    @Inject(method = "init", at = @At("TAIL"))
    private void addExtraHandlers(CallbackInfo ci) {
        addTooltipSubmenuHandler(new BulkGoopSizeTooltipSubmenueHandler(client));
    }
}
