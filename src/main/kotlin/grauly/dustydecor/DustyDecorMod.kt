package grauly.dustydecor

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object DustyDecorMod : ModInitializer {
    const val MODID = "dustydecor"
    val logger = LoggerFactory.getLogger(MODID)

    override fun onInitialize() {
        ModBlocks.init()
        ModItems.init()
        ModItemGroups.init()
        ModSoundEvents.init()
        ModComponentTypes.init()
        ModTooltips.init()
        ModParticleTypes.init()
        ModBlockEntityTypes.init()
        ModStorages.init()
        ModServerPackets.init()
        ModServerPacketReceivers.init()
        ModAttachmentTypes.init()
        ModEvents.init()
    }
}