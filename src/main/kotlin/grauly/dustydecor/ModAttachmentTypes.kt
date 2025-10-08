package grauly.dustydecor

import com.mojang.serialization.Codec
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs

object ModAttachmentTypes {

    val VOID_CONSUMPTION: AttachmentType<Float> = AttachmentRegistry
        .create(Identifier.of(DustyDecorMod.MODID, "void_consumption"))
        { builder ->
            builder
                .persistent(Codecs.NON_NEGATIVE_FLOAT)
                .initializer { 0f }
                .syncWith(PacketCodecs.FLOAT) { target, player -> true }
        }

    fun init() {
        //[Space intentionally left blank]
    }

    private fun <A> registerPersistent(identifier: Identifier, codec: Codec<A>): AttachmentType<A> {
        return AttachmentRegistry.createPersistent(identifier, codec)
    }

    private fun <A> registerPersistent(id: String, codec: Codec<A>): AttachmentType<A> {
        return registerPersistent(Identifier.of(DustyDecorMod.MODID, id), codec)
    }
}