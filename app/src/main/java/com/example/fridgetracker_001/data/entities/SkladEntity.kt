package com.example.fridgetracker_001.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fridgetracker_001.data.DEFAULT_CATEGORY_STATE

//Entity je třída, která definuje strukturu tabulky v databázi. Každá instance entity odpovídá jednomu řádku v tabulce.
@Entity(tableName = "sklad_table")  //@Entity: Označuje, že tato třída je tabulkou v databázi.
data class SkladEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,   // Sloupec, ktery jednoznacne identifikuje radek v tabulce, ID nam identifikuje radek v tabulce.
    //Anotace @PrimaryKey s parametrem autoGenerate = true zajistí, že hodnota id bude automaticky generována při vložení nového záznamu, což usnadňuje správu jedinečných identifikátorů.
    var nazev: String,                                  // Sloupec názvu skladu
    var expirace1: String = "7",                             // Sloupec expirace
    var expirace2: String = "3",                           // Sloupec expirace
    var iconResourceId: Int,                                // volitelné
    var iconPath: String? = null,                       // volitelné - pokud ukládáme do souboru
    var preferovane: Boolean = false,
    val poradi: Int = 1,
    var viewType: String = "LIST",
    var categoryExpansionState: String = DEFAULT_CATEGORY_STATE,
    var sortPotraviny: String = "NAME",      // může být "NAME", "DATE_ADDED", "DATE_EXPIRY"
    var sortKategorie: String = "DEFAULT"    // může být "DEFAULT", "ALPHABETICAL", "COUNT"
)