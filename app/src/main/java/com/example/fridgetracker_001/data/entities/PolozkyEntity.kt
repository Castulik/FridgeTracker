package com.example.fridgetracker_001.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "polozky_katalog")
data class PolozkyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nazev: String,
    val kategorie: String
)
