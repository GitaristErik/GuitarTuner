package com.example.guitartuner.data.tuner

import android.util.Log
import com.example.guitartuner.data.db.AppDatabase
import com.example.guitartuner.data.db.model.PitchCrossRefTable
import com.example.guitartuner.data.db.model.PitchTable
import com.example.guitartuner.data.db.model.ToneTable
import com.example.guitartuner.data.db.model.toPitch
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.domain.entity.common.ChromaticScaleGenerator
import com.example.guitartuner.domain.entity.tuner.Alteration
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.repository.tuner.PitchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PitchRepositoryImpl(
    private val coroutineScope: CoroutineScope,
    private val settingsManager: SettingsManager,
    private val database: AppDatabase
) : PitchRepository {

    private val _purePitchesList by lazy {
        MutableStateFlow(listOf<PitchTable>())
    }
    override val purePitchesList get() = _purePitchesList.asStateFlow()

    private val alteration: Alteration = Alteration.SHARP

    override suspend fun getPitchById(id: Int): Pitch? = database.pitchDAO
        .getPitchWithToneById(id)
        ?.toPitch(alteration)

    override suspend fun findPitchByTone(tone: Tone): Pitch? = database.pitchDAO
        .findPitchByDegreeAndOctave(tone.degree, tone.octave)
        ?.toPitch(alteration)


    init {
        initSettings()
        initPitches()
    }

    private fun initSettings() {
        coroutineScope.launch(Dispatchers.IO) {
            settingsManager.baseFrequency.collectLatest {
                regeneratePitches(it)
            }
        }
    }

    private val isInitialized = MutableStateFlow(false)


    private fun initPitches() {
        coroutineScope.launch(Dispatchers.IO) {
            launch {
                isInitialized.collectLatest { isInit ->
                    Log.e("PitchRepositoryImpl", "isInitialized: $isInit")
                    if (isInit) {
                        database.pitchDAO.getPitches().collectLatest { pitchTables ->
                            Log.e("PitchRepositoryImpl", "pitchTablesCount: ${pitchTables.size}")
                            _purePitchesList.update { pitchTables }
                        }
                    }
                }
            }

            if (database.pitchDAO.count() == 0) {
                regeneratePitches(settingsManager.generalBaseFrequency)
            }
            isInitialized.value = true
        }
    }

    private fun regeneratePitches(referenceFrequency: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            isInitialized.value = false
            database.pitchDAO.deletePitches()

            ChromaticScaleGenerator()
                .generateAsFlow(referenceFrequency.toDouble())
                .collect { (freq, tone) ->
                    val pitchId = async { insertPitch(freq, tone) }
                    val toneIds = async { insertTone(tone) }
                    makeCrossRef(pitchId.await(), toneIds.await())
                }

            // signal that the pitches is initialized
            isInitialized.value = true
        }
    }

    private suspend fun makeCrossRef(pitchId: Long, toneIds: List<Long>) =
        toneIds.map {
            PitchCrossRefTable(
                pitchId = pitchId.toInt(),
                toneId = it.toInt(),
            )
        }.toTypedArray().let {
            database.pitchDAO.insertPitchCrossRef(*it)
        }

    private suspend fun insertPitch(freq: Double, tone: Tone) = database
        .pitchDAO
        .insertPitch(
            PitchTable(
                frequency = freq,
                octave = tone.octave,
                degree = tone.degree
            )
        )

    private suspend fun insertTone(tone: Tone) = Tone.degrees[tone.degree]!!
        .map { (note, alteration) ->
            ToneTable(
                note = note,
                octave = tone.octave,
                alteration = alteration,
                degree = tone.degree
            )
        }.toTypedArray().let {
            database.pitchDAO.insertTone(*it)
        }
}
