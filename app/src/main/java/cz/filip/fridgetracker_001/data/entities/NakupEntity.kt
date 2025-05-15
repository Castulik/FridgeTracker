package cz.filip.fridgetracker_001.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cz.filip.fridgetracker_001.data.DEFAULT_CATEGORY_STATE
import cz.filip.fridgetracker_001.data.SortCategoryOption
import cz.filip.fridgetracker_001.data.SortOption
import cz.filip.fridgetracker_001.data.ViewTypeNakup

@Entity(tableName = "nakup_table")
data class NakupEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var nazev: String,

    // Tady si ukládáš, jaké kategorie jsou rozbalené/sbalené
    var categoryExpansionState: String = DEFAULT_CATEGORY_STATE,
    // Třídění položek
    var sortPolozky: SortOption = SortOption.QUANTITY,
    // Třídění kategorií
    var sortKategorie: SortCategoryOption = SortCategoryOption.COUNT,

    var viewType: ViewTypeNakup = ViewTypeNakup.YELLOW,
)