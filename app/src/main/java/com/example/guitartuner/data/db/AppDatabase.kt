package com.example.guitartuner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.guitartuner.data.db.dao.PitchDAO
import com.example.guitartuner.data.db.dao.TuningSetDAO
import com.example.guitartuner.data.db.model.PitchCrossRefTable
import com.example.guitartuner.data.db.model.PitchTable
import com.example.guitartuner.data.db.model.ToneTable
import com.example.guitartuner.data.db.model.TuningSetCrossRefTable
import com.example.guitartuner.data.db.model.TuningSetTable
import com.example.guitartuner.data.db.model.TuningSetWithPitchesTable

//@TypeConverters(Converters::class)
@Database(
    entities = [
        PitchTable::class,
        PitchCrossRefTable::class,
        ToneTable::class,
        TuningSetTable::class,
        TuningSetCrossRefTable::class],
    views = [TuningSetWithPitchesTable::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract val pitchDAO: PitchDAO

    abstract val tuningSetDAO: TuningSetDAO

    companion object {
        const val DATABASE_NAME = "GuitarTuner_db"
    }
}