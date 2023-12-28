package com.example.guitartuner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.guitartuner.data.db.dao.PitchDAO
import com.example.guitartuner.data.db.model.PitchCrossRefTable
import com.example.guitartuner.data.db.model.PitchTable
import com.example.guitartuner.data.db.model.ToneTable

//@TypeConverters(Converters::class)
@Database(
    entities = [
        PitchTable::class,
        PitchCrossRefTable::class,
//        PitchWithTones::class,
        ToneTable::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract val pitchDAO : PitchDAO

    companion object {
        const val DATABASE_NAME = "GuitarTuner_db"
    }
}