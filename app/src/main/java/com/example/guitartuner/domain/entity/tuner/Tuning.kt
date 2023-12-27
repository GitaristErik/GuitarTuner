package com.example.guitartuner.domain.entity.tuner

import kotlin.math.absoluteValue
import kotlin.math.sign

data class Tuning(
    val closestPitch: Pitch,
    val currentFrequency: Double,
    val deviation: Int,
    val isTuned: Boolean = deviation.absoluteValue < MIN_DEVIATION_FOR_TUNED,
) {
    val normalizedDeviation: Double get() =
        if (deviation.absoluteValue > MAX_DEVIATION)
            deviation.sign.toDouble()
        else
            deviation.toDouble() / 100.0

    companion object {
        const val MAX_DEVIATION = 100.0

        /** Threshold in semitones that note offset must be below to be considered in tune. */
        const val MIN_DEVIATION_FOR_TUNED = 13.0

        /** Time in ms that a note must be held below the threshold for before being considered in tune. */
        const val TUNED_SUSTAIN_TIME = 900
    }
}