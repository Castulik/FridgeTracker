package cz.filip.fridgetracker_001.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cz.filip.fridgetracker_001.data.FoodIcon

@Entity(tableName = "code_table")
data class CodeEntity(
    @PrimaryKey
    var code: String, // Barcode je teď primární klíč
    var nazev: String,
    var druh: String,
    var potravinaIkona: FoodIcon,
)