package grauly.dustydecor.blockentity.vac_station

import net.minecraft.util.StringIdentifiable

interface EnumButtonIdHolder {
    fun getId(): Int
}

enum class CopperGolemMode(val string: String, val buttonId: Int) : StringIdentifiable, EnumButtonIdHolder {
    IGNORE("ignore", 1),
    INTERACT("interact", 2);

    override fun asString(): String = string
    override fun getId(): Int = buttonId

    companion object {
        val CODEC: StringIdentifiable.EnumCodec<CopperGolemMode> =
            StringIdentifiable.createCodec(CopperGolemMode.entries::toTypedArray)
    }
}

enum class RedstoneEmissionMode(val string: String, val buttonId: Int) : StringIdentifiable, EnumButtonIdHolder {
    NONE("none", 3),
    ON_ARRIVAL("on_arrival", 4),
    WHILE_EMPTY("while_empty", 5),
    ON_SEND("on_send", 6);

    override fun asString(): String = string
    override fun getId(): Int = buttonId

    companion object {
        val CODEC: StringIdentifiable.EnumCodec<RedstoneEmissionMode> =
            StringIdentifiable.createCodec(RedstoneEmissionMode.entries::toTypedArray)
    }
}

enum class SendMode(val string: String, val buttonId: Int) : StringIdentifiable, EnumButtonIdHolder {
    MANUAL("manual", 7),
    AUTOMATIC("automatic", 8),
    ON_REDSTONE("on_redstone", 9);

    override fun asString(): String = string
    override fun getId(): Int = buttonId

    companion object {
        val CODEC: StringIdentifiable.EnumCodec<SendMode> =
            StringIdentifiable.createCodec(SendMode.entries::toTypedArray)
    }
}
