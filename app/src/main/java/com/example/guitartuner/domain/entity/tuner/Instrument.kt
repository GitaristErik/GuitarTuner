package com.example.guitartuner.domain.entity.tuner

data class Instrument(
    val instrumentId: Int,
    val countStrings: Int,
    val lowestPitch: Pitch,
    val highestPitch: Pitch
) {
    companion object {

    }
}
