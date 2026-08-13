package com.example.guitartuner.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.guitartuner.data.db.model.TuningSetCrossRefTable
import com.example.guitartuner.data.db.model.TuningSetTable
import com.example.guitartuner.data.db.model.TuningSetWithPitchesTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TuningSetDAO {

    @Transaction
    @Query("SELECT * FROM TuningSetWithPitchesTable WHERE isFavorite = 1")
    fun getFavouritesTunings(): Flow<List<TuningSetWithPitchesTable>>

    @Transaction
    @Query("SELECT * FROM TuningSetWithPitchesTable WHERE tuningId = :tuningId")
    suspend fun getTuningSetById(tuningId: Int): List<TuningSetWithPitchesTable>

    @Query("SELECT Count(*) FROM TuningSetTable")
    fun count(): Int

    @Transaction
    @Query(
        """
    SELECT * FROM TuningSetWithPitchesTable
    WHERE TuningSetWithPitchesTable.instrumentId = :instrumentId
    AND TuningSetWithPitchesTable.pitchId IN (:pitches)
    """
    )
    fun findTuningByPitchIdsAndInstrument(
        pitches: List<Int>,
        instrumentId: Int
    ): List<TuningSetWithPitchesTable>?

    @RawQuery(observedEntities = [TuningSetWithPitchesTable::class])
    fun rawQueryFilterTunings(rawQuery: SimpleSQLiteQuery): Flow<List<TuningSetWithPitchesTable>>

    fun filterTunings(
        isFavorite: Boolean? = null,
        instrumentIds: List<Int>? = null,
        countString: List<Int>? = null,
        start: Int = 0,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<TuningSetWithPitchesTable>> {
        val instrumentIdsQuery = if (instrumentIds?.isEmpty() == true) null
        else instrumentIds?.joinToString(",") { it.toString() }

        val countStringQuery = if (countString?.isEmpty() == true) null
        else countString?.joinToString(",") { it.toString() }

        // Parentheses required so OR does not break out of AND precedence.
        val query = """
        SELECT * FROM TuningSetWithPitchesTable
        WHERE (${isFavorite} IS NULL OR TuningSetWithPitchesTable.isFavorite = ${isFavorite})
        AND ((${instrumentIdsQuery ?: "null"}) IS NULL OR TuningSetWithPitchesTable.instrumentId IN (${instrumentIdsQuery ?: "null"}))
        AND ((${countStringQuery ?: "null"}) IS NULL OR (
            SELECT COUNT(DISTINCT pitchId) FROM TuningSetCrossRefTable
            WHERE TuningSetCrossRefTable.tuningId = TuningSetWithPitchesTable.tuningId
        ) IN (${countStringQuery ?: "null"}))
        LIMIT $limit OFFSET $start """.trimIndent()
        val rawQuery = SimpleSQLiteQuery(query)
        return rawQueryFilterTunings(rawQuery)
    }

    // insert ----------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTuningSet(tuningSet: TuningSetTable): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTuningSetCrossRef(vararg tuningSetCrossRef: TuningSetCrossRefTable)

    // remove ----------------------

    @Query("DELETE FROM TuningSetCrossRefTable WHERE tuningId = :tuningId")
    fun deleteTuningSetCrossRefsById(tuningId: Int)

    @Query("DELETE FROM TuningSetTable WHERE tuningId = :tuningId")
    fun deleteTuningSetTableById(tuningId: Int)

    @Transaction
    fun deleteTuningSetById(tuningId: Int) {
        deleteTuningSetCrossRefsById(tuningId)
        deleteTuningSetTableById(tuningId)
    }

}
