package grauly.dustydecor

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object ModBlockTags {
    val LARGE_VENT_CONNECTABLE: TagKey<Block> =
        TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(DustyDecorMod.MODID, "large_vent_connectable"))
}