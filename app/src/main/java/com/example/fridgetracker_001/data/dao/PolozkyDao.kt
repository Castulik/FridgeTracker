package com.example.fridgetracker_001.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PolozkyDao {

    // Vrací Flow všech položek v katalogu.
    @Query("SELECT * FROM polozky_katalog")
    fun getAllPolozkyFlow(): Flow<List<PolozkyEntity>>

    // Vloží jednu položku (nahrazuje, pokud už existuje).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolozka(polozka: PolozkyEntity)

    // Vloží víc položek naráz.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolozky(polozky: List<PolozkyEntity>)

    // Smaže danou položku.
    @Delete
    suspend fun deletePolozka(polozka: PolozkyEntity)

    // Zjistí počet řádků v tabulce (kvůli zjištění, zda je prázdná).
    @Query("SELECT COUNT(*) FROM polozky_katalog")
    suspend fun getCount(): Int

    @Query("SELECT * FROM polozky_katalog WHERE nazev = :nazev AND kategorie = :kategorie LIMIT 1")
    suspend fun getPolozkaEntity(nazev: String, kategorie: String): PolozkyEntity?

    @Update
    suspend fun updatePolozka(polozka: PolozkyEntity)
}
