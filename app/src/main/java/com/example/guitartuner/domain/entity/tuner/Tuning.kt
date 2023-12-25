package com.example.guitartuner.domain.entity.tuner

import kotlin.math.absoluteValue
import kotlin.math.sign

data class Tuning(
    val closestPitch: Pitch,
    val currentFrequency: Double,
    val deviation: Int,
) {
    val isTuned: Boolean get() = (deviation.absoluteValue < MIN_DEVIATION_FOR_TUNED)

    val normalizedDeviation: Double get() =
        if (deviation.absoluteValue > MAX_DEVIATION)
            deviation.sign.toDouble()
        else
            deviation.toDouble() / 100.0

    companion object {
        const val MAX_DEVIATION = 100.0

        const val MIN_DEVIATION_FOR_TUNED = 13.0
    }
}