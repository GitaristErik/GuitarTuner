package com.example.guitartuner.domain.usecase

import com.example.guitartuner.domain.entity.settings.Settings
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * Pure helpers for tuner offset display and in-tune checks.
 */
object ComputeTuningOffset {

    fun isInTune(deviationCents: Int, minDeviationCents: Int): Boolean =
        deviationCents.absoluteValue < minDeviationCents

    /**
     * Maps raw cent deviation to a normalized [-1, 1] needle position.
     */
    fun normalizeDeviation(deviationCents: Int, maxDeviationCents: Double = 1000.0): Double =
        if (deviationCents.absoluteValue > maxDeviationCents) {
            deviationCents.sign.toDouble()
        } else {
            deviationCents.toDouble() / 100.0
        }

    /**
     * Formats offset for the gauge center label based on display type setting.
     */
    fun formatOffset(
        normalizedDeviation: Double?,
        isTuned: Boolean,
        displayType: Settings.TunerDisplayType,
    ): OffsetDisplay {
        if (normalizedDeviation == null) return OffsetDisplay.Listening
        if (isTuned) return OffsetDisplay.InTune

        val value = when (displayType) {
            Settings.TunerDisplayType.SIMPLE ->
                (normalizedDeviation * displayType.multiplier).roundToInt().toDouble()
            Settings.TunerDisplayType.CENTS ->
                (normalizedDeviation * 100.0)
            Settings.TunerDisplayType.SEMITONES ->
                (normalizedDeviation * 100.0) / displayType.multiplier
        }

        val direction = if (normalizedDeviation < 0) OffsetDirection.TuneUp else OffsetDirection.TuneDown
        return OffsetDisplay.Offset(value = value, direction = direction, displayType = displayType)
    }

    sealed interface OffsetDisplay {
        data object Listening : OffsetDisplay
        data object InTune : OffsetDisplay
        data class Offset(
            val value: Double,
            val direction: OffsetDirection,
            val displayType: Settings.TunerDisplayType,
        ) : OffsetDisplay
    }

    enum class OffsetDirection { TuneUp, TuneDown }
}
