package grauly.dustydecor

import net.minecraft.world.level.block.Block
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.resources.ResourceLocation

object ModBlockTags {
    val LARGE_VENT_CONNECTABLE: TagKey<Block> =
        TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, "large_vent_connectable"))
}