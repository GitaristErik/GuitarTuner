package com.example.guitartuner.domain.repository.tuner

import androidx.lifecycle.LifecycleEventObserver
import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.entity.tuner.Tuning
import kotlinx.coroutines.flow.StateFlow

interface TunerRepository : LifecycleEventObserver {
    val state: StateFlow<Tuning?>

    fun selectTone(tone: Tone)

    var autoMode: Boolean
}
