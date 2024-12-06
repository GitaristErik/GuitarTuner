package com.example.guitartuner.ui.model

data class TuningUIState (
    val tuningId: Int,
    val tuningName: String,
    val notesList: String
) : Comparable<TuningUIState> {
    override fun compareTo(other: TuningUIState): Int {
        return tuningName.compareTo(other.tuningName)
    }
}