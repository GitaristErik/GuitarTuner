package com.example.guitartuner.domain.repository.tuner

import com.example.guitartuner.data.db.model.PitchTable
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Tone
import kotlinx.coroutines.flow.StateFlow

interface PitchRepository {

    val purePitchesList: StateFlow<List<PitchTable>>
//    val pitchList: StateFlow<List<Pitch>>

    suspend fun getPitchById(id: Int): Pitch?

    suspend fun findPitchByTone(tone: Tone): Pitch?
}