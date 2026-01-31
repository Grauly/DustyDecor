package grauly.dustydecor.mixin;

import grauly.dustydecor.block.voidgoop.LayerThresholdSpreadingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityPlacementItemStackMixin {

    @Shadow private BlockState blockState;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/context/DirectionalPlaceContext;<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)V"), index = 3)
    private ItemStack stub(ItemStack par4) {
        if (blockState.getBlock() instanceof LayerThresholdSpreadingBlock) {
            return blockState.getBlock().asItem().getDefaultInstance();
        }
        return par4;
    }
}
