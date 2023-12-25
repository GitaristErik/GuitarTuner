package com.example.guitartuner.domain.repository.tuner

import com.example.guitartuner.domain.entity.tuner.Instrument
import com.example.guitartuner.domain.entity.tuner.TuningSet
import kotlinx.coroutines.flow.StateFlow

interface TuningSetsRepository {

    val favoritesTuningSets: StateFlow<List<TuningSet>>

    val currentInstrument: StateFlow<Instrument>

    val currentTuningSet: StateFlow<TuningSet>

//    fun getTuningSetsAll(): List<TuningSet>
//    fun getTuningSetsFavorites(): List<TuningSet>
//    fun getTuningSetById(id: Int): TuningSet
//    fun getTuningBySettings(): TuningSet
    fun selectTuning(tuningId: Int)

    fun tuneUpString(stringId: Int, semitones: Int = 1)
    fun tuneDownString(stringId: Int, semitones: Int = 1)
    fun tuneUpTuning(semitones: Int = 1)
    fun tuneDownTuning(semitones: Int = 1)
}