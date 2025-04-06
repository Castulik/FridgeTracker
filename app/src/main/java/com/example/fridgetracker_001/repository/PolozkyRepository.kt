package com.example.fridgetracker_001.repository

import com.example.fridgetracker_001.data.dao.PolozkyDao
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import kotlinx.coroutines.flow.Flow

class PolozkyRepository(
    private val dao: PolozkyDao
) {
    // Flow – v Compose můžeme používat collectAsState
    val polozkyFlow: Flow<List<PolozkyEntity>> = dao.getAllPolozkyFlow()

    // Vložit jednu položku do katalogu
    suspend fun vlozitPolozka(p: PolozkyEntity) {
        dao.insertPolozka(p)
    }

    // Smazat jednu položku z katalogu
    suspend fun smazatPolozku(p: PolozkyEntity) {
        dao.deletePolozka(p)
    }

    // Funkce pro hromadný insert, kdybys ji potřeboval
    suspend fun vlozitVicePolozek(polozky: List<PolozkyEntity>) {
        dao.insertPolozky(polozky)
    }

    suspend fun getPolozkaEntity(nazev: String, kategorie: String): PolozkyEntity? {
        return dao.getPolozkaEntity(nazev, kategorie)
    }

    // Prvotní naplnění (seed) tabulky, pokud je prázdná.
    suspend fun initialSeedIfEmpty() {
        val count = dao.getCount()
        if (count == 0) {
            val defaultItems = listOf(
                PolozkyEntity(nazev = "Jablka", kategorie = "Ovoce a Zelenina"),
                PolozkyEntity(nazev = "Banány", kategorie = "Ovoce a Zelenina"),
                PolozkyEntity(nazev = "Mléko", kategorie = "Mléčné výrobky"),
                PolozkyEntity(nazev = "Máslo", kategorie = "Mléčné výrobky"),
                PolozkyEntity(nazev = "Kuřecí prsa", kategorie = "Maso a Ryby"),
                PolozkyEntity(nazev = "Těstoviny", kategorie = "Trvanlivé"),
                PolozkyEntity(nazev = "Chléb", kategorie = "Pečivo"),
                PolozkyEntity(nazev = "Vejce", kategorie = "Vejce"),
            )
            dao.insertPolozky(defaultItems)
        }
    }

    suspend fun updatePolozka(polozka: PolozkyEntity) {
        dao.updatePolozka(polozka)
    }
}
