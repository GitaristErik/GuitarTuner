package com.example.guitartuner.domain.repository.tuner

import androidx.lifecycle.LifecycleEventObserver

interface PitchGenerationRepository : LifecycleEventObserver {

    fun playStringSelectSound(string: Int)

    fun playInTuneSound(string: Int)
}