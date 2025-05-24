package cz.filip.fridgetracker_001.repository

import cz.filip.fridgetracker_001.data.dao.SkladDao
import cz.filip.fridgetracker_001.data.entities.SkladEntity
import kotlinx.coroutines.flow.Flow

/*
* Repository  slouží jako prostředník mezi datovou vrstvou (např. databází, API)
* a dalšími vrstvami aplikace (např. ViewModel).
* V Kotlinu a Jetpack Compose se často používá jako místo, kde jsou definovány operace pro práci s daty.
*/

class SkladRepository(private val skladDao: SkladDao) {

    // Přidat nový sklad
    suspend fun vlozitSklad(sklad: SkladEntity) {
        skladDao.vlozitSklad(sklad)
    }

    // Získání konkrétního skladu podle jeho ID
    suspend fun ziskejSkladPodleId(skladId: Int): SkladEntity? {
        return skladDao.ziskejIdSklad(skladId)
    }

    // Získání všech skladů z databáze
    suspend fun ziskejSklady(): List<SkladEntity> {
        return skladDao.ziskejSklady()
    }

    // Smazání konkrétního skladu
    suspend fun smazatSklad(sklad: SkladEntity) {
        skladDao.smazatSklad(sklad)
    }

    // Aktualizace skladu v databázi
    suspend fun aktualizovatSklad(sklad: SkladEntity) {
        skladDao.aktualizovatSklad(sklad)
    }

    fun getSkladFlowById(skladId: Int): Flow<SkladEntity?> {
        return skladDao.getSkladFlowById(skladId)
    }
}