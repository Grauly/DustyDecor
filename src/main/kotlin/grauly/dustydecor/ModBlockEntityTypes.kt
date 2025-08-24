package grauly.dustydecor

import grauly.dustydecor.blockentity.TallCageLampBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModBlockEntityTypes {

    val TALL_CAGE_LAMP_ENTITY: BlockEntityType<TallCageLampBlockEntity> = register("tall_cage_lamp", ::TallCageLampBlockEntity, *ModBlocks.TALL_CAGE_LAMPS.toTypedArray())

    private fun <T : BlockEntity> register(
        id: String,
        blockEntityFactory: FabricBlockEntityTypeBuilder.Factory<out T>,
        vararg blocks: Block
    ): BlockEntityType<T> {
        return register(Identifier.of(DustyDecorMod.MODID, id), blockEntityFactory, *blocks)
    }

    private fun <T : BlockEntity> register(
        id: Identifier,
        blockEntityFactory: FabricBlockEntityTypeBuilder.Factory<out T>,
        vararg blocks: Block
    ): BlockEntityType<T> {
        return Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            id,
            FabricBlockEntityTypeBuilder.create<T>(blockEntityFactory, *blocks).build()
        )
    }

    fun init() {
        //[Space intentionally left blank]
    }
}