package grauly.dustydecor

import grauly.dustydecor.generators.*
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.slf4j.LoggerFactory

object DustyDecorDatagen : DataGeneratorEntrypoint {
    val logger = LoggerFactory.getLogger("${DustyDecorMod.MODID}-datagen")
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        BlockDatagenWrapper.init()
        ItemDatagenWrapper.init()
        val pack = fabricDataGenerator.createPack()
        pack.addProvider(::BlockModelDatagen)
        pack.addProvider(::ItemModelDatagen)
        pack.addProvider(::BlockTagDatagen)
        pack.addProvider(::ItemTagDatagen)
        pack.addProvider(::BlockLootTableDatagen)
        pack.addProvider(::RecipeDatagen)
        pack.addProvider(::LangDatagen)
        pack.addProvider(::SoundEventDatagen)
    }

    override fun getEffectiveModId(): String {
        return DustyDecorMod.MODID
    }
}