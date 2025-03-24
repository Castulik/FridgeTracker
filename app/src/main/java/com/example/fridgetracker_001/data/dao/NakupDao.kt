package com.example.fridgetracker_001.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fridgetracker_001.data.entities.NakupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NakupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun vlozitNakup(nakup: NakupEntity)

    @Delete
    suspend fun smazatNakup(nakup: NakupEntity)

    @Update
    suspend fun aktualizovatNakup(nakup: NakupEntity)

    // Pro získání Flow jednoho konkrétního nákupu
    @Query("SELECT * FROM nakup_table WHERE id = :nakupId")
    fun getNakupFlowById(nakupId: Int): Flow<NakupEntity?>

    // Varianta pro získání všech nákupů:
    @Query("SELECT * FROM nakup_table")
    fun getAllNakupyFlow(): Flow<List<NakupEntity>>

    // Pro jednorázové získání konkrétního nákupu (např. v asynchronní akci):
    @Query("SELECT * FROM nakup_table WHERE id = :nakupId")
    suspend fun getNakupById(nakupId: Int): NakupEntity?

    @Query("SELECT * FROM nakup_table ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLastUpdatedNakup(): NakupEntity?
}
