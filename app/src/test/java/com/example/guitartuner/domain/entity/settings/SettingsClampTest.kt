package com.example.guitartuner.domain.entity.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsClampTest {

    @Test
    fun clampBaseFrequency_bounds() {
        assertEquals(420, Settings.clampBaseFrequency(400))
        assertEquals(460, Settings.clampBaseFrequency(500))
        assertEquals(440, Settings.clampBaseFrequency(440))
    }

    @Test
    fun clampDeviation_bounds() {
        assertEquals(3, Settings.clampDeviation(1))
        assertEquals(90, Settings.clampDeviation(200))
        assertEquals(15, Settings.clampDeviation(15))
    }
}
