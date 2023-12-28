package com.example.guitartuner.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PitchTable (
    @PrimaryKey(autoGenerate = true)
    val pitchId: Int = 0,
    val frequency: Double,
    val octave: Int,
    val degree: Int,
)

