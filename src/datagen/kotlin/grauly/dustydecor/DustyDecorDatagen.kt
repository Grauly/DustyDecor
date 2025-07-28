package grauly.dustydecor

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.slf4j.LoggerFactory

object DustyDecorDatagen : DataGeneratorEntrypoint {
	val logger = LoggerFactory.getLogger("${DustyDecorMod.MODID}-datagen")
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()
		pack.addProvider(::BlockModelDatagen)
		pack.addProvider(::ItemModelDatagen)
	}

	override fun getEffectiveModId(): String {
		return DustyDecorMod.MODID
	}
}