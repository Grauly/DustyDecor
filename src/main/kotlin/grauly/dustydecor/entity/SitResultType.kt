package grauly.dustydecor.entity

import grauly.dustydecor.DustyDecorMod

enum class SitResultType(val messageTranslationKey: String, val success: Boolean, val shouldDisplayMessage: Boolean) {
    SUCCESS("sittable.${DustyDecorMod.MODID}.success", true, false),
    OCCUPIED("sittable.${DustyDecorMod.MODID}.occupied", false, true),
    ALREADY_SITTING("sittable.${DustyDecorMod.MODID}.already_sitting", false, true),
    TOO_FAR("sittable.${DustyDecorMod.MODID}.too_far", false, true),
    NONE("sittable.${DustyDecorMod.MODID}.none", false, false),
}