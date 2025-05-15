package cz.filip.fridgetracker_001.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cz.filip.fridgetracker_001.data.FoodIcon
import cz.filip.fridgetracker_001.data.entities.PotravinaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PotravinaDao {
    @Insert
    suspend fun pridatPotravinu(potravina: PotravinaEntity)

    @Update
    suspend fun aktualizovatPotravinu(potravina: PotravinaEntity)

    @Delete
    suspend fun smazatPotravinu(potravina: PotravinaEntity)

    @Query("SELECT * FROM potravina_table WHERE skladId = :skladId")
    fun getPotravinaBySkladId(skladId: Int): Flow<List<PotravinaEntity>>

    @Query("SELECT * FROM potravina_table WHERE id = :potravinaId")
    fun getPotravinaFlowById(potravinaId: Int): Flow<PotravinaEntity?>

    //Poznámka: Můžeš klidně vracet Flow<List<PotravinaEntity>> místo List<PotravinaEntity> podle toho, jestli to potřebuješ asynchronně pozorovat.
    // Pro Worker (jednorázový dotaz) ale stačí obyčejné List<PotravinaEntity>.
    @Query("SELECT * FROM potravina_table")
    suspend fun getAllPotraviny(): List<PotravinaEntity>

    // Nové – varianta jako Flow, abychom mohli sledovat změny:
    @Query("SELECT * FROM potravina_table")
    fun getAllPotravinyFlow(): Flow<List<PotravinaEntity>>

    // NOVÁ funkce, která hromadně změní název/druh/ikonu všem potravinám se stejným kódem:
    @Query("""
        UPDATE potravina_table
        SET nazev = :newNazev,
            druh = :newDruh,
            potravinaIkona = :newIcon
        WHERE code = :kod
    """)
    suspend fun aktualizovatVsechnySDanymKodem(
        kod: String,
        newNazev: String,
        newDruh: String,
        newIcon: FoodIcon
    )
}