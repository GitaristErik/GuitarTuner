package com.example.guitartuner.domain.repository.tuner

import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Tone
import kotlinx.coroutines.flow.StateFlow

interface PitchRepository {

    val pitchList: StateFlow<List<Pitch>>

    fun getPitchById(id: Int): Pitch

    fun findPitchByTone(tone: Tone): Pitch
}