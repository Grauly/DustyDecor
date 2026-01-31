package grauly.dustydecor

import grauly.dustydecor.entity.SeatEntity
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

object ModEntities {

    val SEAT_ENTITY: EntityType<SeatEntity> = registerEntity(
        ::SeatEntity,
        "seat",
        EntityType.Builder
            .of(::SeatEntity, MobCategory.MISC)
            .noLootTable()
            .sized(0f, 0f)
    )

    fun init() {
        // [Space intentionally left blank]
    }

    private fun <T: Entity> registerEntity(constructor: (EntityType<T>, Level) -> T, id: String, builder: EntityType.Builder<T>): EntityType<T> {
        return registerEntity(constructor, ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, id), builder)
    }

    private fun <T: Entity> registerEntity(constructor: (EntityType<T>, Level) -> T, id: ResourceLocation, builder: EntityType.Builder<T>): EntityType<T> {
        val registryKey = ResourceKey.create(Registries.ENTITY_TYPE, id)
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, registryKey, builder.build(registryKey))
    }
}