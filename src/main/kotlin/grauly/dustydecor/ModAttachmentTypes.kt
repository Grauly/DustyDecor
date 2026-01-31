package grauly.dustydecor

import com.mojang.serialization.Codec
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs

object ModAttachmentTypes {

    val VOID_CONSUMPTION: AttachmentType<Float> = AttachmentRegistry
        .create(ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, "void_consumption"))
        { builder ->
            builder
                .persistent(ExtraCodecs.NON_NEGATIVE_FLOAT)
                .initializer { 0f }
                .syncWith(ByteBufCodecs.FLOAT) { target, player -> true }
        }

    fun init() {
        //[Space intentionally left blank]
    }

    private fun <A> registerPersistent(identifier: ResourceLocation, codec: Codec<A>): AttachmentType<A> {
        return AttachmentRegistry.createPersistent(identifier, codec)
    }

    private fun <A> registerPersistent(id: String, codec: Codec<A>): AttachmentType<A> {
        return registerPersistent(ResourceLocation.fromNamespaceAndPath(DustyDecorMod.MODID, id), codec)
    }
}