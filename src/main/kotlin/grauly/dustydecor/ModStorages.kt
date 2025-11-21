package grauly.dustydecor

import grauly.dustydecor.blockentity.VacPipeBlockEntity
import grauly.dustydecor.blockentity.vac_station.VacPipeStationBlockEntity
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.minecraft.util.math.Direction

object ModStorages {
    fun init() {
        ItemStorage.SIDED.registerForBlockEntity({ be, direction ->
            if (direction == null) return@registerForBlockEntity null
            if (be !is VacPipeBlockEntity) return@registerForBlockEntity null
            if (be.getInsertDirection() == direction) return@registerForBlockEntity be.storage
            if (be.getExtractDirection() == direction) return@registerForBlockEntity be.storage
            return@registerForBlockEntity null
        }, ModBlockEntityTypes.VAC_PIPE_ENTITY)

        ItemStorage.SIDED.registerForBlockEntity({ be, direction ->
            if (direction == null) return@registerForBlockEntity null
            if (be !is VacPipeStationBlockEntity) return@registerForBlockEntity null
            if (direction == Direction.UP) return@registerForBlockEntity be.storage
            return@registerForBlockEntity null
        }, ModBlockEntityTypes.VAC_PIPE_STATION_ENTITY)
    }
}