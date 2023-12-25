package com.example.guitartuner.domain.repository.tuner

import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.entity.tuner.Tuning
import kotlinx.coroutines.flow.StateFlow

interface TunerRepository {
    val state: StateFlow<Tuning?>

    fun selectTone(tone: Tone)
    var autoMode: Boolean
}
