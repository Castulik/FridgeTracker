package com.example.fridgetracker_001.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fridgetracker_001.data.entities.PotravinaEntity
import com.example.fridgetracker_001.data.entities.SkladEntity
import kotlinx.coroutines.flow.Flow

//@Dao: Anotace označující rozhraní jako Data Access Object (DAO) v rámci knihovny Room. Definuje rozhrani, ktere obsahuje metody pro pristup k databazi
//@Insert, @Update, @Delete: Standardní operace nad databází.
//@Query: Umožňuje psát vlastní SQL dotazy.

//suspend = Funkce označené jako suspend lze pozastavit a obnovit bez blokování aktuálního vlákna.
//Deklarace rozhraní s názvem SkladDao. Definuje pravidla pro práci s entitou SkladEntity v databázi.

@Dao
interface SkladDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun vlozitSklad(sklad: SkladEntity)

    @Query("SELECT * FROM sklad_table WHERE id = :skladId")
    suspend fun ziskejIdSklad(skladId: Int): SkladEntity?

    @Query("SELECT * FROM sklad_table")
    suspend fun ziskejSklady(): List<SkladEntity>

    @Delete
    suspend fun smazatSklad(sklad: SkladEntity)

    @Update
    suspend fun aktualizovatSklad(sklad: SkladEntity)

    @Query("SELECT * FROM sklad_table WHERE id = :skladId")
    fun getSkladFlowById(skladId: Int): Flow<SkladEntity?>
}