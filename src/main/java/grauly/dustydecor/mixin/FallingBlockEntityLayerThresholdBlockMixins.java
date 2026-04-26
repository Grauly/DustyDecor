package grauly.dustydecor.mixin;

import com.llamalad7.mixinextras.expression.Definition;import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import grauly.dustydecor.block.layered.LayerThresholdSpreadingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityLayerThresholdBlockMixins {

    @Shadow private BlockState blockState;

    @Shadow
    public boolean dropItem;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/context/DirectionalPlaceContext;<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)V"), index = 3)
    private ItemStack ensurePlacementContextItem(ItemStack par4) {
        if (blockState.getBlock() instanceof LayerThresholdSpreadingBlock) {
            return blockState.getBlock().asItem().getDefaultInstance();
        }
        return par4;
    }

    @Definition(id = "dropItem", field = "Lnet/minecraft/world/entity/item/FallingBlockEntity;dropItem:Z")
    @Expression("this.dropItem")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean notifyBlockOfNonDrop(boolean original) {
        if (!original && blockState.getBlock() instanceof LayerThresholdSpreadingBlock layerThresholdSpreadingBlock) {
            layerThresholdSpreadingBlock.onDestroyedByFall(
                    ((FallingBlockEntity) ((Object) this)).level(),
                    ((FallingBlockEntity) ((Object) this)).blockPosition(),
                    blockState
                    );
        }
        return original;
    }
}
