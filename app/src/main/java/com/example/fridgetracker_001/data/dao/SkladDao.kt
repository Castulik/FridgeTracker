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

@Dao
//@Dao: Anotace označující rozhraní jako Data Access Object (DAO) v rámci knihovny Room. Definuje rozhrani, ktere obsahuje metody pro pristup k databazi
//@Insert, @Update, @Delete: Standardní operace nad databází.
//@Query: Umožňuje psát vlastní SQL dotazy.

//suspend = Funkce označené jako suspend lze pozastavit a obnovit bez blokování aktuálního vlákna.
//Deklarace rozhraní s názvem SkladDao. Definuje pravidla pro práci s entitou SkladEntity v databázi.
interface SkladDao {
    //Anotace označující vkládací operaci do databáze. OnConflict určuje strategii při konfliktu (duplicita ID) -> Existujici zaznam se nahradi novym.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun vlozitSklad(sklad: SkladEntity) // vlozi novy zaznam do tabulky sklad_table

    //Anotace označující metodu jako dotaz do databáze s konkrétním SQL příkazem.
    //SELECT * FORM sklad_table = Vybere všechny radky a sloupce z tabulky sklad_table. (*) - znamena vsechny sloupce.
    //:skladId pojmenovany parametr, ktery umoznuje dynamicke vkladani hodnot do SQL dotazu.
    @Query("SELECT * FROM sklad_table WHERE id = :skladId")
    suspend fun ziskejIdSklad(skladId: Int): SkladEntity? //Deklarace suspendované funkce ziskejIdSklad, která přijímá skladId typu Int a vrací objekt SkladEntity nebo null

    //Anotace označující metodu jako dotaz do databáze s konkrétním SQL příkazem.
    //Definuje SQL dotaz, který vybírá všechny sloupce ze všech záznamů v tabulce sklad_table.
    @Query("SELECT * FROM sklad_table")
    suspend fun ziskejSklady(): List<SkladEntity> //Deklarace suspendované funkce getAllSklady, která nebere žádné parametry a vrací seznam objektů SkladEntity.

    //Anotace označující metodu jako mazací operaci v databázi.
    //Označuje, že metoda bude odstraňovat záznamy z databáze na základě poskytnutých entit.
    @Delete
    suspend fun smazatSklad(sklad: SkladEntity) //funkce prijima SkladEntity, ktery nasledne odstranim z databaze

    //Anotace označující metodu jako operaci aktualizace záznamu v databázi.
    //Automaticky vygeneruje SQL příkaz, který aktualizuje řádek v tabulce odpovídající primárnímu klíči (@PrimaryKey) objektu, který je předán jako parametr.
    @Update
    suspend fun aktualizovatSklad(sklad: SkladEntity)

    @Query("SELECT * FROM sklad_table WHERE id = :skladId")
    fun getSkladFlowById(skladId: Int): Flow<SkladEntity?>
}