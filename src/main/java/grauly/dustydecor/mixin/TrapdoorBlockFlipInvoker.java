package grauly.dustydecor.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TrapdoorBlock.class)
public interface TrapdoorBlockFlipInvoker {
    @Invoker("flip")
    void flip(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player);
}
