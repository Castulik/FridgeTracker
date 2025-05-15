package com.example.fridgetracker_001.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fridgetracker_001.data.DEFAULT_CATEGORY_STATE
import com.example.fridgetracker_001.data.SkladIcon

//Entity je třída, která definuje strukturu tabulky v databázi. Každá instance entity odpovídá jednomu řádku v tabulce.

@Entity(tableName = "sklad_table")
data class SkladEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var nazev: String,
    var expirace1: String = "7",
    var expirace2: String = "3",
    var icon: SkladIcon,
    var iconPath: String? = null,
    var preferovane: Boolean = false,
    val poradi: Int = 1,
    var viewType: String = "LIST",
    var categoryExpansionState: String = DEFAULT_CATEGORY_STATE,
    var sortPotraviny: String = "NAME",      // může být "NAME", "DATE_ADDED", "DATE_EXPIRY"
    var sortKategorie: String = "DEFAULT"    // může být "DEFAULT", "ALPHABETICAL", "COUNT"
)