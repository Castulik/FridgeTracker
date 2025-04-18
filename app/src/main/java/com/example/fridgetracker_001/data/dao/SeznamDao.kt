package com.example.fridgetracker_001.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fridgetracker_001.data.entities.SeznamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeznamDao {
    @Insert
    suspend fun pridatPolozku(seznamEntity: SeznamEntity)

    @Delete
    suspend fun smazatPolozku(seznamEntity: SeznamEntity)

    @Update
    suspend fun updatePolozku(seznamEntity: SeznamEntity)

    @Query("SELECT * FROM seznam")
    fun getAll(): Flow<List<SeznamEntity>>

    @Query("SELECT * FROM seznam WHERE nazev = :nazev AND kategorie = :kategorie AND nakupId = :nakupId LIMIT 1")
    suspend fun getSeznamEntity(nazev: String, kategorie: Int, nakupId: Int): SeznamEntity?

}