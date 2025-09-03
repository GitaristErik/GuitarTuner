package com.example.guitartuner.data.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import androidx.room.Index
import com.example.guitartuner.domain.entity.tuner.Alteration
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Note

@Entity(primaryKeys = ["pitchId", "toneId"], indices = [Index("toneId"), Index("pitchId")])
data class PitchCrossRefTable (
    var pitchId: Int,
    var toneId: Int,
)

//@Entity(primaryKeys = ["toneId"])
data class PitchWithTones(
    @Embedded var pitch: PitchTable,
    @Relation(
        parentColumn = "pitchId",
        entityColumn = "toneId",
        associateBy = Junction(PitchCrossRefTable::class)
    )
    var tones: List<ToneTable>
)

fun PitchWithTones.toPitch(alteration: Alteration): Pitch = Pitch(
    id = pitch.pitchId,
    frequency = pitch.frequency,
    tone = run {
        // Prefer linked tones; choose based on requested alteration (first = sharp side, last = flat/natural).
        val fromRelation = if (tones.isNotEmpty()) {
            if (alteration == Alteration.SHARP) tones.first() else tones.last()
        } else null

        val chosen = fromRelation ?: run {
            // Fallback: derive from degree and octave if relation rows aren't ready yet (e.g., during regeneration).
            val acc = com.example.guitartuner.domain.entity.tuner.Tone.degrees[pitch.degree].orEmpty()
            val (note, alt) = when {
                acc.isNotEmpty() && alteration == Alteration.SHARP -> acc.first()
                acc.isNotEmpty() -> acc.last()
                else -> Note.A to Alteration.NATURAL
            }
            ToneTable(
                note = note,
                octave = pitch.octave,
                alteration = alt,
                degree = pitch.degree
            )
        }
        chosen.toTone()
    },
)