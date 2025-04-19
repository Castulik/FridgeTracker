package com.example.fridgetracker_001.repository

import android.content.Context
import androidx.compose.ui.res.stringResource
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.IconRegistry
import com.example.fridgetracker_001.data.IconRegistry.KindOption
import com.example.fridgetracker_001.data.KindOptionEnum
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

    suspend fun getPolozkaEntity(nazev: String, kategorie: String): PolozkyEntity? {
        return dao.getPolozkaEntity(nazev, kategorie)
    }

    // Prvotní naplnění (seed) tabulky, pokud je prázdná.
    suspend fun initialSeedIfEmpty() {
        val count = dao.getCount()
        if (count == 0) {
            val defaultItems = listOf(
                PolozkyEntity(nazev = context.getString(R.string.item_frozen_pizza), kategorie = KindOptionEnum.FROZEN.name),
                PolozkyEntity(nazev = context.getString(R.string.item_ice_cream), kategorie = KindOptionEnum.FROZEN.name),
                PolozkyEntity(nazev = context.getString(R.string.item_canned_beans), kategorie = KindOptionEnum.NONPERISHABLE.name),
                PolozkyEntity(nazev = context.getString(R.string.item_pasta), kategorie = KindOptionEnum.NONPERISHABLE.name),
                PolozkyEntity(nazev = context.getString(R.string.item_apples), kategorie = KindOptionEnum.FRUIT_VEG.name),
                PolozkyEntity(nazev = context.getString(R.string.item_carrots), kategorie = KindOptionEnum.FRUIT_VEG.name),
                PolozkyEntity(nazev = context.getString(R.string.item_milk), kategorie = KindOptionEnum.DAIRY.name),
                PolozkyEntity(nazev = context.getString(R.string.item_cheese), kategorie = KindOptionEnum.DAIRY.name),
                PolozkyEntity(nazev = context.getString(R.string.item_chicken_breast), kategorie = KindOptionEnum.MEAT_FISH.name),
                PolozkyEntity(nazev = context.getString(R.string.item_salmon), kategorie = KindOptionEnum.MEAT_FISH.name),
                PolozkyEntity(nazev = context.getString(R.string.item_bread), kategorie = KindOptionEnum.BAKERY.name),
                PolozkyEntity(nazev = context.getString(R.string.item_croissant), kategorie = KindOptionEnum.BAKERY.name),
                PolozkyEntity(nazev = context.getString(R.string.item_eggs), kategorie = KindOptionEnum.EGGS.name),
                PolozkyEntity(nazev = context.getString(R.string.item_quail_eggs), kategorie = KindOptionEnum.EGGS.name),
                PolozkyEntity(nazev = context.getString(R.string.item_lentils), kategorie = KindOptionEnum.GRAINS_LEGUMES.name),
                PolozkyEntity(nazev = context.getString(R.string.item_rice), kategorie = KindOptionEnum.GRAINS_LEGUMES.name),
                PolozkyEntity(nazev = context.getString(R.string.item_ham), kategorie = KindOptionEnum.DELI.name),
                PolozkyEntity(nazev = context.getString(R.string.item_salami), kategorie = KindOptionEnum.DELI.name),
                PolozkyEntity(nazev = context.getString(R.string.item_water), kategorie = KindOptionEnum.DRINKS.name),
                PolozkyEntity(nazev = context.getString(R.string.item_orange_juice), kategorie = KindOptionEnum.DRINKS.name),
                PolozkyEntity(nazev = context.getString(R.string.item_lasagna), kategorie = KindOptionEnum.READY_MEALS.name),
                PolozkyEntity(nazev = context.getString(R.string.item_curry_rice), kategorie = KindOptionEnum.READY_MEALS.name),
                PolozkyEntity(nazev = context.getString(R.string.item_ketchup), kategorie = KindOptionEnum.OTHER.name),
                PolozkyEntity(nazev = context.getString(R.string.item_mayonnaise), kategorie = KindOptionEnum.OTHER.name),
            )
            dao.insertPolozky(defaultItems)
        }
    }

    suspend fun updatePolozka(polozka: PolozkyEntity) {
        dao.updatePolozka(polozka)
    }
}
