package grauly.dustydecor

import grauly.dustydecor.blockentity.VacPipeBlockEntity
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage

object ModStorages {
    fun init() {
        ItemStorage.SIDED.registerForBlockEntity({ be, direction ->
            if (direction == null) return@registerForBlockEntity null
            if (be !is VacPipeBlockEntity) return@registerForBlockEntity null
            if (be.getInsertDirection() == direction) return@registerForBlockEntity be.storage
            if (be.getExtractDirection() == direction) return@registerForBlockEntity be.storage
            return@registerForBlockEntity null
        }, ModBlockEntityTypes.VAC_PIPE_ENTITY)
    }
}