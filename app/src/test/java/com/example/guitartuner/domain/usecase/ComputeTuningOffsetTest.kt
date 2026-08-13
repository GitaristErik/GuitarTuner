package com.example.guitartuner.domain.usecase

import com.example.guitartuner.domain.entity.settings.Settings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ComputeTuningOffsetTest {

    @Test
    fun isInTune_whenWithinThreshold() {
        assertTrue(ComputeTuningOffset.isInTune(deviationCents = 5, minDeviationCents = 10))
        assertFalse(ComputeTuningOffset.isInTune(deviationCents = 15, minDeviationCents = 10))
    }

    @Test
    fun normalizeDeviation_clampsBeyondMax() {
        assertEquals(1.0, ComputeTuningOffset.normalizeDeviation(2000), 0.0)
        assertEquals(-1.0, ComputeTuningOffset.normalizeDeviation(-2000), 0.0)
        assertEquals(0.5, ComputeTuningOffset.normalizeDeviation(50), 0.0)
    }

    @Test
    fun formatOffset_listeningWhenNull() {
        val result = ComputeTuningOffset.formatOffset(
            normalizedDeviation = null,
            isTuned = false,
            displayType = Settings.TunerDisplayType.SIMPLE,
        )
        assertEquals(ComputeTuningOffset.OffsetDisplay.Listening, result)
    }

    @Test
    fun formatOffset_inTune() {
        val result = ComputeTuningOffset.formatOffset(
            normalizedDeviation = 0.01,
            isTuned = true,
            displayType = Settings.TunerDisplayType.CENTS,
        )
        assertEquals(ComputeTuningOffset.OffsetDisplay.InTune, result)
    }

    @Test
    fun formatOffset_tuneUpWhenFlat() {
        val result = ComputeTuningOffset.formatOffset(
            normalizedDeviation = -0.2,
            isTuned = false,
            displayType = Settings.TunerDisplayType.SIMPLE,
        ) as ComputeTuningOffset.OffsetDisplay.Offset

        assertEquals(ComputeTuningOffset.OffsetDirection.TuneUp, result.direction)
        assertEquals(-2.0, result.value, 0.0)
    }

    @Test
    fun formatOffset_tuneDownWhenSharp() {
        val result = ComputeTuningOffset.formatOffset(
            normalizedDeviation = 0.4,
            isTuned = false,
            displayType = Settings.TunerDisplayType.CENTS,
        ) as ComputeTuningOffset.OffsetDisplay.Offset

        assertEquals(ComputeTuningOffset.OffsetDirection.TuneDown, result.direction)
        assertEquals(40.0, result.value, 0.0)
    }
}
