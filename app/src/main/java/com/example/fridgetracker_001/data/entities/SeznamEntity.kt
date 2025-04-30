package com.example.fridgetracker_001.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fridgetracker_001.data.DEFAULT_CATEGORY_STATE

@Entity(
    tableName = "seznam",
    foreignKeys = [
        ForeignKey(
            entity = NakupEntity::class,
            parentColumns = ["id"],
            childColumns = ["nakupId"],
            onDelete = ForeignKey.CASCADE // pokud smažeš NakupEntity, smažou se i položky
        )
    ],
    indices = [Index("nakupId")] // Index pro rychlejší JOIN
)
data class SeznamEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nazev: String,
    val polozkaId: Int? = null,
    val kategorie: String,
    val checked: Boolean = false,

    val nakupId: Int,
    val quantity: Int = 1
)