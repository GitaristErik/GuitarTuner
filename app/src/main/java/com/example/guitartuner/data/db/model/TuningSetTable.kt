package com.example.guitartuner.data.db.model

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.guitartuner.domain.entity.tuner.Alteration
import com.example.guitartuner.domain.entity.tuner.Note
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.entity.tuner.TuningSet

@Entity
data class TuningSetTable(
    @PrimaryKey(autoGenerate = true) var tuningId: Int,
    val name: String,
    val instrumentId: Int,
    val isFavorite: Boolean = false,
)

@Entity(primaryKeys = ["tuningId", "pitchId"])
data class TuningSetCrossRefTable(
    var tuningId: Int,
    var pitchId: Int,
)

@DatabaseView(
    """
    SELECT ts.tuningId, ts.name, ts.instrumentId, ts.isFavorite, 
        pt.pitchId, pt.frequency, 
        tt.toneId, tt.note, tt.octave, tt.alteration, tt.degree 
    FROM TuningSetTable as ts
    INNER JOIN TuningSetCrossRefTable as tsr ON ts.tuningId = tsr.tuningId
    INNER JOIN PitchTable as pt ON tsr.pitchId = pt.pitchId
    INNER JOIN PitchCrossRefTable as pcr ON pt.pitchId = pcr.pitchId
    INNER JOIN ToneTable as tt ON pcr.toneId = tt.toneId
"""
)
data class TuningSetWithPitchesTable(
    val tuningId: Int,
    val name: String,
    val instrumentId: Int,
    val isFavorite: Boolean,
    val pitchId: Int,
    val frequency: Double,
    val toneId: Int,
    val note: Note,
    val octave: Int,
    val alteration: Alteration,
    val degree: Int,
)

fun List<TuningSetWithPitchesTable>.toTuningSet(alteration: Alteration) =
    groupBy { it.tuningId }.map { (tuningId, tuningSetWithPitches) ->
    TuningSet(
        tuningId = tuningId,
        name = tuningSetWithPitches.firstNotNullOf { it.name },
        pitches = tuningSetWithPitches.groupBy { it.pitchId }.map { (pitchId, pitches) ->
            Pitch(
                id = pitchId,
                frequency = pitches.first().frequency,
                tone = Tone(
                    note = pitches.first().note,
                    octave = pitches.first().octave,
                    alteration = (alteration == Alteration.SHARP).let {
                        if (it) pitches.first() else pitches.last()
                    }.alteration,
                    degree = pitches.first().degree,
                ),
            )
        },
        instrumentId = tuningSetWithPitches.firstNotNullOf { it.instrumentId },
        isFavorite = tuningSetWithPitches.firstNotNullOf { it.isFavorite },
    )

}
fun TuningSet.toTuningSetTable() = TuningSetTable(
    tuningId = tuningId,
    name = name,
    instrumentId = instrumentId,
    isFavorite = isFavorite,
)

fun TuningSet.toTuningSetCrossRefTable() = pitches.map { pitch ->
    TuningSetCrossRefTable(
        tuningId = tuningId,
        pitchId = pitch.id,
    )
}