package com.example.guitartuner.ui.model

import com.example.guitartuner.domain.entity.tuner.Notation
import com.example.guitartuner.domain.entity.tuner.Pitch

data class TuneButtonsUIState(
    val tuningId: Int,
    val tuningName: String,
    val pitchMap: Map<Int, Pitch>,
    val notation: Notation
) {

    constructor(
        tuningId: Int,
        tuningName: String,
        pitchList: List<Pitch>,
        notation: Notation
    ) : this(
        tuningId,
        tuningName,
        pitchMap = pitchList.mapIndexed { i, p -> i to p }.toMap(),
        notation
    )

    companion object {
        val initialState = TuneButtonsUIState(
            tuningId = 0,
            tuningName = "",
            pitchMap = emptyMap(),
            notation = Notation.English
        )
    }

    fun getNoteFullName(index: Int) = pitchMap[index]?.run {
        "${notation.convertFromNote(note)}${octave}"
    }
}