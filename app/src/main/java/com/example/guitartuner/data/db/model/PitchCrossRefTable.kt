package com.example.guitartuner.data.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import com.example.guitartuner.domain.entity.tuner.Alteration
import com.example.guitartuner.domain.entity.tuner.Pitch

@Entity(primaryKeys = ["pitchId", "toneId"])
data class PitchCrossRefTable (
    val pitchId: Int,
    val toneId: Int,
)

//@Entity(primaryKeys = ["toneId"])
data class PitchWithTones(
    @Embedded val pitch: PitchTable,
    @Relation(
        parentColumn = "pitchId",
        entityColumn = "toneId",
        associateBy = Junction(PitchCrossRefTable::class)
    )
    val tones: List<ToneTable>
)

fun PitchWithTones.toPitch(alteration: Alteration): Pitch = Pitch(
    id = pitch.pitchId,
    frequency = pitch.frequency,
    tone = (alteration == Alteration.SHARP).let {
        if(it) tones.first() else tones.last()
    }.toTone(),
)