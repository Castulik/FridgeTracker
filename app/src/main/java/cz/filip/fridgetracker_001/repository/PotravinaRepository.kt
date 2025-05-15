package cz.filip.fridgetracker_001.repository

import cz.filip.fridgetracker_001.data.FoodIcon
import cz.filip.fridgetracker_001.data.dao.PotravinaDao
import cz.filip.fridgetracker_001.data.entities.PotravinaEntity
import kotlinx.coroutines.flow.Flow

/*
* Repository je designový vzor, který slouží jako prostředník mezi datovou vrstvou (např. databází, API)
* a dalšími vrstvami aplikace (např. ViewModel).
* V Kotlinu a Jetpack Compose se často používá jako místo, kde jsou definovány operace pro práci s daty.
*/

class PotravinaRepository(private val potravinaDao: PotravinaDao) {

    // Přidat nový sklad
    suspend fun pridatPotravinu(potravina: PotravinaEntity) {
        potravinaDao.pridatPotravinu(potravina)
    }

    // Smazání konkrétního skladu
    suspend fun smazatPotravinu(potravina: PotravinaEntity) {
        potravinaDao.smazatPotravinu(potravina)
    }

    // Aktualizace skladu v databázi
    suspend fun aktualizovatPotravinu(potravina: PotravinaEntity) {
        potravinaDao.aktualizovatPotravinu(potravina)
    }

    //Získání potravin pro konkrétní sklad (jako Flow)
    fun getPotravinyBySkladId(skladId: Int): Flow<List<PotravinaEntity>> {
        return potravinaDao.getPotravinaBySkladId(skladId)
    }

    fun getPotravinaFlowById(potravinaId: Int): Flow<PotravinaEntity?> {
        return potravinaDao.getPotravinaFlowById(potravinaId)
    }

    suspend fun getAllPotraviny(): List<PotravinaEntity> {
        return potravinaDao.getAllPotraviny()
    }

    fun getAllPotravinyFlow(): Flow<List<PotravinaEntity>> {
        return potravinaDao.getAllPotravinyFlow()
    }

    // Nová hromadná aktualizace:
    suspend fun aktualizovatVsechnySDanymKodem(
        kod: String,
        newNazev: String,
        newDruh: String,
        newIcon: FoodIcon
    ) {
        potravinaDao.aktualizovatVsechnySDanymKodem(kod, newNazev, newDruh, newIcon)
    }
}