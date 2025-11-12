package grauly.dustydecor.mixin;

import grauly.dustydecor.block.voidgoop.LayerThresholdSpreadingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityPlacementItemStackMixin {

    @Shadow private BlockState blockState;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/AutomaticItemPlacementContext;<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/Direction;)V"), index = 3)
    private ItemStack stub(ItemStack par4) {
        if (blockState.getBlock() instanceof LayerThresholdSpreadingBlock) {
            return blockState.getBlock().asItem().getDefaultStack();
        }
        return par4;
    }
}
