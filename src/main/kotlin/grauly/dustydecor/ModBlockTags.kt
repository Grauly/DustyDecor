package grauly.dustydecor

import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object ModBlockTags {
    val LARGE_VENT_CONNECTABLE: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, Identifier.of(DustyDecorMod.MODID, "large_vent_connectable"))
}