package grauly.dustydecor

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.slf4j.LoggerFactory

object DustyDecorDatagen : DataGeneratorEntrypoint {
	val logger = LoggerFactory.getLogger("${DustyDecorMod.MODID}-datagen")
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()
		pack.addProvider(::ModBlockModelDatagen)
		pack.addProvider(::ModItemModelDatagen)
	}

	override fun getEffectiveModId(): String {
		return DustyDecorMod.MODID
	}
}