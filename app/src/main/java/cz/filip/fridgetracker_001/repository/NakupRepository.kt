package cz.filip.fridgetracker_001.repository

import cz.filip.fridgetracker_001.data.dao.NakupDao
import cz.filip.fridgetracker_001.data.entities.NakupEntity
import kotlinx.coroutines.flow.Flow

class NakupRepository(private val nakupDao: NakupDao) {

    suspend fun vlozitNakup(nakup: NakupEntity): Long {
        return nakupDao.vlozitNakup(nakup)
    }

    suspend fun smazatNakup(nakup: NakupEntity) {
        nakupDao.smazatNakup(nakup)
    }

    suspend fun aktualizovatNakup(nakup: NakupEntity) {
        nakupDao.aktualizovatNakup(nakup)
    }

    fun getNakupFlowById(nakupId: Int): Flow<NakupEntity?> {
        return nakupDao.getNakupFlowById(nakupId)
    }

    fun getAllNakupyFlow(): Flow<List<NakupEntity>> {
        return nakupDao.getAllNakupyFlow()
    }

    suspend fun getNakupById(nakupId: Int): NakupEntity? {
        return nakupDao.getNakupById(nakupId)
    }
}
