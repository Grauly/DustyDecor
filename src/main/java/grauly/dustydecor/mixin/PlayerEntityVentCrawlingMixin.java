package grauly.dustydecor.mixin;

import grauly.dustydecor.DustyDecorMod;
import grauly.dustydecor.ModBlocks;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityVentCrawlingMixin {

    @Unique
    private boolean isPlayerInVent(PlayerEntity player) {
        return player.getEntityWorld().getBlockState(player.getBlockPos()).isOf(ModBlocks.INSTANCE.getVENT());
    }

    @Inject(method = "canChangeIntoPose", at = @At("HEAD"), cancellable = true)
    private void forceVentCrawling(EntityPose pose, CallbackInfoReturnable<Boolean> cir) {
        if (pose == EntityPose.SWIMMING) {
            if (isPlayerInVent(((PlayerEntity) (Object) this))) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getExpectedPose", at = @At("HEAD"), cancellable = true)
    private void expectVentCrawling(CallbackInfoReturnable<EntityPose> cir) {
        if (isPlayerInVent(((PlayerEntity) (Object) this))) {
            cir.setReturnValue(EntityPose.SWIMMING);
        }
    }
}
