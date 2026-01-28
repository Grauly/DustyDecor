package grauly.dustydecor

import grauly.dustydecor.entity.SeatEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World

object ModEntities {

    val SEAT_ENTITY: EntityType<SeatEntity> = registerEntity(
        ::SeatEntity,
        "seat",
        EntityType.Builder
            .create(::SeatEntity, SpawnGroup.MISC)
            .dropsNothing()
            .dimensions(0f, 0f)
    )

    fun init() {
        // [Space intentionally left blank]
    }

    private fun <T: Entity> registerEntity(constructor: (EntityType<T>, World) -> T, id: String, builder: EntityType.Builder<T>): EntityType<T> {
        return registerEntity(constructor, Identifier.of(DustyDecorMod.MODID, id), builder)
    }

    private fun <T: Entity> registerEntity(constructor: (EntityType<T>, World) -> T, id: Identifier, builder: EntityType.Builder<T>): EntityType<T> {
        val registryKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id)
        return Registry.register(Registries.ENTITY_TYPE, registryKey, builder.build(registryKey))
    }
}