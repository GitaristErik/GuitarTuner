package com.example.guitartuner.ui.model

import com.example.guitartuner.domain.entity.tuner.Alteration
import com.example.guitartuner.domain.entity.tuner.Instrument
import com.example.guitartuner.domain.entity.tuner.Notation
import com.example.guitartuner.domain.entity.tuner.Tone

data class TuneButtonsUIState(
    val toneMap: Map<Int, Tone>,
    val notation: Notation,
    val instrument: Instrument,
) {
    val tuningName: String = toneMap.values.joinToString(separator = "") {
        it.note.name + it.octave + when (it.alteration) {
            Alteration.SHARP -> "#"
            Alteration.FLAT -> "b"
            Alteration.NATURAL -> ""
        }
    }

    constructor(
        toneList: List<Tone>,
        notation: Notation,
        instrument: Instrument
    ) : this(
        toneMap = toneList.mapIndexed { i, p -> i to p }.toMap(),
        notation,
        instrument
    )

    fun getNoteFullName(index: Int) = toneMap[index]?.run {
        "${notation.convertFromNote(note)}${when (alteration) {
            Alteration.SHARP -> "#"
            Alteration.FLAT -> "b"
            Alteration.NATURAL -> ""
        }} $octave"
    }
}