package grauly.dustydecor.blockentity

import grauly.dustydecor.ModBlockEntityTypes
import grauly.dustydecor.block.furniture.SingleFurnitureBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

class PhoneBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(ModBlockEntityTypes.PHONE_ENTITY, worldPosition, blockState), ItemOwner {
    override fun level(): Level = level!!

    override fun position(): Vec3 = worldPosition.bottomCenter

    override fun getVisualRotationYInDegrees(): Float = blockState.getValue(SingleFurnitureBlock.ROTATION) * 16 / 360f
}