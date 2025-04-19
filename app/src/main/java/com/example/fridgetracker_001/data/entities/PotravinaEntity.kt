package com.example.fridgetracker_001.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "potravina_table",
    foreignKeys = [
        ForeignKey(
            entity = SkladEntity::class,
            parentColumns = ["id"],
            childColumns = ["skladId"],
            onDelete = ForeignKey.CASCADE // Klíčové nastavení pro automatické mazání
        )
    ],
    indices = [Index("skladId")] // Index pro optimalizaci
)
data class PotravinaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Jedinečný identifikátor potraviny
    var nazev: String,                               // Název potraviny
    var mnozstvi: String,                               // mnozstvi potraviny
    var datumSpotreby: String,                       // Datum spotřeby ve formátu "YYYY-MM-DD"
    var datumPridani: String,                           // Datum přidání do skladu ve formátu "YYYY-MM-DD"
    var potravinaIconaId: Int,                          // ID ikony potraviny
    var vaha: String,
    var jednotky: String,
    var poznamka: String,
    var druh: String,
    var code: String,
    var skladId: Int,                                // ID skladu, cizí klíč odkazující na SkladEntity
)