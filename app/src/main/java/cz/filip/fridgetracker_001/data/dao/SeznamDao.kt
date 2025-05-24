package cz.filip.fridgetracker_001.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cz.filip.fridgetracker_001.data.entities.SeznamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeznamDao {
    @Insert
    suspend fun pridatPolozku(seznamEntity: SeznamEntity)

    @Delete
    suspend fun smazatPolozku(seznamEntity: SeznamEntity)

    @Update
    suspend fun updatePolozku(seznamEntity: SeznamEntity)

    // Vrací všechny položky z tabulky 'seznam' jako asynchronní tok dat (Flow),
    // což umožňuje automatické aktualizace v UI při změně dat.
    @Query("SELECT * FROM seznam")
    fun getAll(): Flow<List<SeznamEntity>>

    // Hledá konkrétní položku podle názvu, kategorie a ID nákupu.
    // Pokud ji nenajde, vrací null.
    @Query("SELECT * FROM seznam WHERE nazev = :nazev AND kategorie = :kategorie AND nakupId = :nakupId LIMIT 1")
    suspend fun getSeznamEntity(nazev: String, kategorie: String, nakupId: Int): SeznamEntity?

    // Vrací položky z tabulky 'seznam' pro daný nákup, seřazené podle zadaných kritérií.
    // Řazení kategorií:
    //  - 'ALPHABETICAL' = podle názvu kategorie
    //  - 'COUNT' = podle počtu položek v dané kategorii
    //  - 'DEFAULT' = bez specifického řazení kategorií
    // Řazení položek uvnitř kategorií:
    //  - 'NAME' = podle názvu položky (abecedně, bez rozlišení velikosti)
    //  - 'QUANTITY' = podle množství (sestupně)
    //  - 'DEFAULT' = bez specifického řazení
    @Query(
        """
        SELECT  s.*,
                (SELECT COUNT(*) FROM seznam 
                 WHERE kategorie = s.kategorie AND nakupId = :nakupId) AS catCount
        FROM   seznam s
        WHERE  s.nakupId = :nakupId
        ORDER BY
              CASE WHEN :catSort = 'ALPHABETICAL' THEN s.kategorie END                 ASC,
              CASE WHEN :catSort = 'COUNT'        THEN catCount    END                 DESC,
              CASE WHEN :itemSort = 'NAME'        THEN s.nazev     END COLLATE NOCASE  ASC,
              CASE WHEN :itemSort = 'QUANTITY'    THEN s.quantity  END                 DESC
        """
    )
    fun getSeznamSorted(
        nakupId: Int,
        catSort: String,
        itemSort: String
    ): Flow<List<SeznamEntity>>
}