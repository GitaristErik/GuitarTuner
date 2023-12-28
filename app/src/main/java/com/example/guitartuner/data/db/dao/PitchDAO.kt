package com.example.guitartuner.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.guitartuner.data.db.model.PitchCrossRefTable
import com.example.guitartuner.data.db.model.PitchTable
import com.example.guitartuner.data.db.model.PitchWithTones
import com.example.guitartuner.data.db.model.ToneTable
import kotlinx.coroutines.flow.Flow

@Dao
interface PitchDAO {

    @Query("SELECT * FROM PitchTable")
    fun getPitches(): Flow<List<PitchTable>>

    @Transaction
    @Query("SELECT * FROM PitchTable WHERE pitchId = :pitchId")
    suspend fun getPitchWithToneById(pitchId: Int): PitchWithTones

    @Transaction
    @Query("SELECT * FROM PitchTable WHERE octave = :octave AND degree = :degree")
    suspend fun findPitchByDegreeAndOctave(degree: Int, octave: Int): PitchWithTones?


    @Query(
        "SELECT (SELECT COUNT(*) FROM PitchTable) + " +
                "(SELECT COUNT(*) FROM ToneTable) + " +
                "(SELECT COUNT(*) FROM PitchCrossRefTable)"
    )
    suspend fun count(): Int


    // insert ----------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPitch(pitch: PitchTable): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTone(vararg tone: ToneTable): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPitchCrossRef(vararg pitchCrossRef: PitchCrossRefTable)


    // remove ----------------------
    @Query("DELETE FROM PitchTable")
    suspend fun deleteAllFromPitchTable()

    @Query("DELETE FROM ToneTable")
    suspend fun deleteAllFromToneTable()

    @Query("DELETE FROM PitchCrossRefTable")
    suspend fun deleteAllFromPitchCrossRefTable()

    suspend fun deleteAll() {
        deleteAllFromPitchTable()
        deleteAllFromToneTable()
        deleteAllFromPitchCrossRefTable()
    }

}