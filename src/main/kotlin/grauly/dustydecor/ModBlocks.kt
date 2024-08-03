package grauly.dustydecor

import grauly.dustydecor.block.VentBlock
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ModBlocks {

    val VENT : Block = registerBlock(VentBlock(), "vent")

    private fun registerBlock(block : Block, id : String, namespace : String = DustyDecorMod.MODID) : Block {
        return Registry.register(Registries.BLOCK, Identifier.of(namespace, id), block)
    }

    fun init() {
        //[Space intentionally left blank]
    }
}