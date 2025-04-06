package com.example.fridgetracker_001.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fridgetracker_001.data.DEFAULT_CATEGORY_STATE

@Entity(tableName = "nakup_table")
data class NakupEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var nazev: String,

    // Tady si ukládáš, jaké kategorie jsou rozbalené/sbalené
    var categoryExpansionState: String = DEFAULT_CATEGORY_STATE,

    // Třídění kategorií
    var sortKategorie: String = "DEFAULT",

    // Třídění položek
    var sortPolozky: String = "NAME",
)