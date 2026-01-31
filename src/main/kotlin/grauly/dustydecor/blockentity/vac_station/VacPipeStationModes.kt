package grauly.dustydecor.blockentity.vac_station

import net.minecraft.util.StringRepresentable


enum class CopperGolemMode(val string: String) : StringRepresentable {
    IGNORE("ignore"),
    INTERACT("interact");

    override fun getSerializedName(): String = string

    companion object {
        val CODEC: StringRepresentable.EnumCodec<CopperGolemMode> =
            StringRepresentable.fromEnum(CopperGolemMode.entries::toTypedArray)
    }
}

enum class RedstoneEmissionMode(val string: String) : StringRepresentable {
    NONE("none"),
    ON_RECEIVE("on_receive"),
    WHILE_EMPTY("while_empty"),
    ON_SEND("on_send");

    override fun getSerializedName(): String = string

    companion object {
        val CODEC: StringRepresentable.EnumCodec<RedstoneEmissionMode> =
            StringRepresentable.fromEnum(RedstoneEmissionMode.entries::toTypedArray)
    }
}

enum class SendMode(val string: String) : StringRepresentable {
    MANUAL("manual"),
    AUTOMATIC("automatic"),
    ON_REDSTONE("on_redstone");

    override fun getSerializedName(): String = string

    companion object {
        val CODEC: StringRepresentable.EnumCodec<SendMode> =
            StringRepresentable.fromEnum(SendMode.entries::toTypedArray)
    }
}
