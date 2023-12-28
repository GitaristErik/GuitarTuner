package com.example.guitartuner.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.guitartuner.domain.entity.tuner.Alteration
import com.example.guitartuner.domain.entity.tuner.Note
import com.example.guitartuner.domain.entity.tuner.Tone

@Entity
data class ToneTable(
    @PrimaryKey(autoGenerate = true)
    var toneId: Int = 0,
    val note: Note,
    val octave: Int,
    val alteration: Alteration,
    val degree: Int,
)

fun ToneTable.toTone() = Tone(
    note = note,
    octave = octave,
    alteration = alteration,
    degree = degree
)
