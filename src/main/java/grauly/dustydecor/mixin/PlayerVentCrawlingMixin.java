package grauly.dustydecor.mixin;

import grauly.dustydecor.ModBlocks;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerVentCrawlingMixin {

    @Unique
    private boolean isPlayerInVent(Player player) {
        return player.level().getBlockState(player.blockPosition()).is(ModBlocks.INSTANCE.getVENT());
    }

    @Inject(method = "canPlayerFitWithinBlocksAndEntitiesWhen", at = @At("HEAD"), cancellable = true)
    private void forceVentCrawling(Pose pose, CallbackInfoReturnable<Boolean> cir) {
        if (pose == Pose.SWIMMING) {
            if (isPlayerInVent(((Player) (Object) this))) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getDesiredPose", at = @At("HEAD"), cancellable = true)
    private void expectVentCrawling(CallbackInfoReturnable<Pose> cir) {
        if (isPlayerInVent(((Player) (Object) this))) {
            cir.setReturnValue(Pose.SWIMMING);
        }
    }
}
