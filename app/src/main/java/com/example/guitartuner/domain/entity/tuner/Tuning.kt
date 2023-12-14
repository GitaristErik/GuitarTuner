package com.example.guitartuner.domain.entity.tuner

data class Tuning(
    val tuningId: Int,
    val pitches: List<Pitch>,
    val instrumentId: Int,
) {

    fun isValid(instrument: Instrument): Boolean {
        return true
    }

    companion object {}
}
