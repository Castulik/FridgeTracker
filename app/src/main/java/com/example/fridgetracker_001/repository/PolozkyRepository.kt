package com.example.fridgetracker_001.repository

import android.content.Context
import androidx.compose.ui.res.stringResource
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.IconRegistry.KindOption
import com.example.fridgetracker_001.data.dao.PolozkyDao
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import kotlinx.coroutines.flow.Flow

class PolozkyRepository(
    private val dao: PolozkyDao,
    private val context: Context
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

    suspend fun getPolozkaEntity(nazev: String, kategorie: Int): PolozkyEntity? {
        return dao.getPolozkaEntity(nazev, kategorie)
    }

    // Prvotní naplnění (seed) tabulky, pokud je prázdná.
    suspend fun initialSeedIfEmpty() {
        val count = dao.getCount()
        if (count == 0) {
            val defaultItems = listOf(
                PolozkyEntity(nazev = context.getString(R.string.item_apples), kategorie = R.string.kind_fruit_veg),
                PolozkyEntity(nazev = context.getString(R.string.item_milk), kategorie = R.string.kind_dairy),
                PolozkyEntity(nazev = context.getString(R.string.item_chicken_breast), kategorie = R.string.kind_meat_fish),
                PolozkyEntity(nazev = context.getString(R.string.item_eggs), kategorie = R.string.kind_eggs),
            )
            dao.insertPolozky(defaultItems)
        }
    }

    suspend fun updatePolozka(polozka: PolozkyEntity) {
        dao.updatePolozka(polozka)
    }
}
