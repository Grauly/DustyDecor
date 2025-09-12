package grauly.dustydecor.mixin.client;

import grauly.dustydecor.DustyDecorMod;
import grauly.dustydecor.particle.custom.CustomParticle;
import grauly.dustydecor.particle.custom.CustomParticleRenderer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Mutable
    @Final
    @Shadow
    private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;

    static {
        PARTICLE_TEXTURE_SHEETS = getSheets();
    }

    @Unique
    private static List<ParticleTextureSheet> getSheets() {
        List<ParticleTextureSheet> sheets = new ArrayList<>(PARTICLE_TEXTURE_SHEETS);
        sheets.add(CustomParticle.Companion.getCUSTOM_TEXTURE_SHEET());
        return sheets;
    }

    @Inject(method = "createParticleRenderer", at = @At("HEAD"), cancellable = true)
    public void addCustomRenderer(ParticleTextureSheet textureSheet, CallbackInfoReturnable<ParticleRenderer<?>> cir) {
        if (textureSheet == CustomParticle.Companion.getCUSTOM_TEXTURE_SHEET()) {
            DustyDecorMod.INSTANCE.getLogger().info("added renderer");
            cir.setReturnValue(new CustomParticleRenderer((ParticleManager) ((Object) this)));
        }
    }
}
