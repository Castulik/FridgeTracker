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

    @Query("SELECT * FROM seznam")
    fun getAll(): Flow<List<SeznamEntity>>

    @Query("SELECT * FROM seznam WHERE nazev = :nazev AND kategorie = :kategorie AND nakupId = :nakupId LIMIT 1")
    suspend fun getSeznamEntity(nazev: String, kategorie: String, nakupId: Int): SeznamEntity?

    @Query(
        """
        SELECT  s.*,
                (SELECT COUNT(*) FROM seznam 
                 WHERE kategorie = s.kategorie AND nakupId = :nakupId) AS catCount
        FROM   seznam s
        WHERE  s.nakupId = :nakupId
        ORDER BY
              /* řazení kategorií */
              CASE WHEN :catSort = 'ALPHABETICAL' THEN s.kategorie END                 ASC,
              CASE WHEN :catSort = 'COUNT'        THEN catCount    END                 DESC,
              /* řazení položek uvnitř kategorie */
              CASE WHEN :itemSort = 'NAME'        THEN s.nazev     END COLLATE NOCASE  ASC,
              CASE WHEN :itemSort = 'QUANTITY'    THEN s.quantity  END                 DESC
        """
    )
    fun getSeznamSorted(
        nakupId: Int,
        catSort: String,      // „ALPHABETICAL“ | „COUNT“ | „DEFAULT“
        itemSort: String      // „NAME“        | „QUANTITY“ | „DEFAULT“
    ): Flow<List<SeznamEntity>>
}