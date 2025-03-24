package com.example.fridgetracker_001.repository

import com.example.fridgetracker_001.data.dao.SeznamDao
import com.example.fridgetracker_001.data.entities.SeznamEntity
import kotlinx.coroutines.flow.Flow

class SeznamRepository(private val seznamDao: SeznamDao) {

    // Stream všech položek v seznamu (jako Flow).
    val seznamy: Flow<List<SeznamEntity>> = seznamDao.getAll()

    suspend fun pridatPolozku(polozka: SeznamEntity) {
        seznamDao.pridatPolozku(polozka)
    }

    suspend fun smazatPolozku(item: SeznamEntity) {
        seznamDao.smazatPolozku(item)
    }

    suspend fun updatePolozku(item: SeznamEntity) {
        seznamDao.updatePolozku(item)
    }

    suspend fun getItemByNazevKategorie(nazev: String, kategorie: String, nakupId: Int): SeznamEntity? {
        return seznamDao.getItemByNazevKategorie(nazev, kategorie, nakupId)
    }

}