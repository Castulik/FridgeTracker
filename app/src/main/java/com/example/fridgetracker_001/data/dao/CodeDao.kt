package com.example.fridgetracker_001.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fridgetracker_001.data.entities.CodeEntity

@Dao
interface CodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun vlozitAktualizovat(codeEntity: CodeEntity)

    @Query("SELECT * FROM code_table WHERE code = :barcode LIMIT 1")
    suspend fun getCodeEntityByCode(barcode: String): CodeEntity?
}