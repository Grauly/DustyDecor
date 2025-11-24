package grauly.dustydecor.blockentity.vac_station

import net.minecraft.util.StringIdentifiable


enum class CopperGolemMode(val string: String) : StringIdentifiable {
    IGNORE("ignore"),
    INTERACT("interact");

    override fun asString(): String = string

    companion object {
        val CODEC: StringIdentifiable.EnumCodec<CopperGolemMode> =
            StringIdentifiable.createCodec(CopperGolemMode.entries::toTypedArray)
    }
}

enum class RedstoneEmissionMode(val string: String) : StringIdentifiable {
    NONE("none"),
    ON_RECEIVE("on_receive"),
    WHILE_EMPTY("while_empty"),
    ON_SEND("on_send");

    override fun asString(): String = string

    companion object {
        val CODEC: StringIdentifiable.EnumCodec<RedstoneEmissionMode> =
            StringIdentifiable.createCodec(RedstoneEmissionMode.entries::toTypedArray)
    }
}

enum class SendMode(val string: String) : StringIdentifiable{
    MANUAL("manual"),
    AUTOMATIC("automatic"),
    ON_REDSTONE("on_redstone");

    override fun asString(): String = string

    companion object {
        val CODEC: StringIdentifiable.EnumCodec<SendMode> =
            StringIdentifiable.createCodec(SendMode.entries::toTypedArray)
    }
}
