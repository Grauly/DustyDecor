package grauly.dustydecor.mixin.client;

import grauly.dustydecor.component.BulkGoopSizeTooltipSubmenueHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenSubmenueHandlerAdder extends Screen {

    protected AbstractContainerScreenSubmenueHandlerAdder(Component title) {
        super(title);
    }

    @Shadow
    protected abstract void addItemSlotMouseAction(ItemSlotMouseAction handler);

    @Inject(method = "init", at = @At("TAIL"))
    private void addExtraHandlers(CallbackInfo ci) {
        addItemSlotMouseAction(new BulkGoopSizeTooltipSubmenueHandler(minecraft));
    }
}
