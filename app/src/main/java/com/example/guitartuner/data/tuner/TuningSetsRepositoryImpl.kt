package com.example.guitartuner.data.tuner

import com.example.guitartuner.domain.entity.tuner.Instrument
import com.example.guitartuner.domain.entity.tuner.Note
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.entity.tuner.TuningSet
import com.example.guitartuner.domain.repository.tuner.PitchRepository
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository.TuningFilterBuilder
import com.example.guitartuner.ui.tuner.components.previewInstrument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TuningSetsRepositoryImpl(
    private val pitchRepository: PitchRepository,
    private val coroutineScope: CoroutineScope
) : TuningSetsRepository {


    private var fakeTuningSets = listOf(
        // "Standard" - "E2, A2, D3, G3, B3, E4",
        TuningSet(
            tuningId = 0,
            name = "Standard",
            instrumentId = 0,
            pitches = listOf(
                Pitch(id = 29, frequency = 82.41, tone = Tone(Note.E, 2)),
                Pitch(id = 34, frequency = 110.0, tone = Tone(Note.A, 2)),
                Pitch(id = 39, frequency = 146.83, tone = Tone(Note.D, 3)),
                Pitch(id = 44, frequency = 196.0, tone = Tone(Note.G, 3)),
                Pitch(id = 48, frequency = 246.94, tone = Tone(Note.B, 3)),
                Pitch(id = 53, frequency = 329.63, tone = Tone(Note.E, 4)),
            ),
        )
    )
    /* listOf(
         // "Standard" - "E2, A2, D3, G3, B3, E4",
         TuningSet(
             tuningId = 0,
             name = "Standard",
             instrumentId = 0,
             pitches = listOfNotNull(
                 pitchRepository.findPitchByTone(Tone(Note.E, 2)),
                 pitchRepository.findPitchByTone(Tone(Note.A, 2)),
                 pitchRepository.findPitchByTone(Tone(Note.D, 3)),
                 pitchRepository.findPitchByTone(Tone(Note.G, 3)),
                 pitchRepository.findPitchByTone(Tone(Note.B, 3)),
                 pitchRepository.findPitchByTone(Tone(Note.E, 4)),
             ),
         ),
         // "Half Step Down (D#)" to "D#2, G#2, C#3, F#3, A#3, D#4",
         TuningSet(
             tuningId = 1,
             instrumentId = 0,
             name = "Half Step Down (D#)",
             pitches = listOfNotNull(
                 pitchRepository.findPitchByTone(Tone(Note.D, 2, alteration = Alteration.SHARP)),
                 pitchRepository.findPitchByTone(Tone(Note.G, 2, alteration = Alteration.SHARP)),
                 pitchRepository.findPitchByTone(Tone(Note.C, 3, alteration = Alteration.SHARP)),
                 pitchRepository.findPitchByTone(Tone(Note.F, 3, alteration = Alteration.SHARP)),
                 pitchRepository.findPitchByTone(Tone(Note.A, 3, alteration = Alteration.SHARP)),
                 pitchRepository.findPitchByTone(Tone(Note.D, 4, alteration = Alteration.SHARP)),
             ),
         )
     )*/


    private val _favoritesTuningSets by lazy {
        MutableStateFlow(
            tuningsList.value.mapNotNull { if (it.first.isFavorite) it.first else null }
        )
    }
    override val favoritesTuningSets: StateFlow<List<TuningSet>> get() = _favoritesTuningSets.asStateFlow()


    override val currentInstrument: StateFlow<Instrument> =
        MutableStateFlow(previewInstrument).asStateFlow()

    private val _currentTuningSet by lazy {
        MutableStateFlow(fakeTuningSets[0])
    }
    override val currentTuningSet
        get() = _currentTuningSet.asStateFlow()


    override fun selectTuning(tuningId: Int) {
        _currentTuningSet.value = fakeTuningSets[tuningId]
    }

    init {
        coroutineScope.launch {
            delay(1000)
            initFavoritesTuningSetsCollector()
        }
        coroutineScope.launch {
            delay(1000)
            initCurrentTuningObserver()
        }
    }

    private suspend fun initCurrentTuningObserver() {
        _tuningsList.collectLatest { tuning ->
            _currentTuningSet.update { cur ->
                fakeTuningSets.find { it.tuningId == cur.tuningId }
                    ?: cur.copy(tuningId = -1, name = "Custom")
            }
        }
    }

    private suspend fun initFavoritesTuningSetsCollector() {
        _tuningsList.collectLatest { tunings ->
            _favoritesTuningSets.value =
                tunings.mapNotNull { if (it.first.isFavorite) it.first else null }
        }
    }

    private fun findTuning(pitches: List<Pitch>, instrumentId: Int) = fakeTuningSets
        .firstOrNull {
            it.instrumentId == instrumentId &&
                    it.pitches.hashCode() == pitches.hashCode()
        }


    private suspend fun updateTune(
        stringId: Int?, semitones: Int,
        tuneAction: suspend (Pitch, Int) -> Pitch?
    ) {
        _currentTuningSet.update { tuning ->
            val pitches = tuning.pitches.mapIndexed { index, pitch ->
                if (index == stringId || stringId == null) {
                    tuneAction(pitch, semitones) ?: return@update tuning
                } else {
                    pitch
                }
            }

            findTuning(pitches, tuning.instrumentId) ?: tuning.copy(
                tuningId = -1, pitches = pitches, name = "Custom"
            )
        }
    }

    override suspend fun tuneUpString(stringId: Int, semitones: Int) =
        updateTune(stringId, semitones, ::pitchTuneUp)

    override suspend fun tuneDownString(stringId: Int, semitones: Int) =
        updateTune(stringId, semitones, ::pitchTuneDown)

    override suspend fun tuneUpTuning(semitones: Int) =
        updateTune(null, semitones, ::pitchTuneUp)
    override suspend fun tuneDownTuning(semitones: Int) =
        updateTune(null, semitones, ::pitchTuneDown)

    private suspend fun pitchTuneUp(pitch: Pitch, semitones: Int) =
        pitchRepository.getPitchById(pitch.id + semitones)

    private suspend fun pitchTuneDown(pitch: Pitch, semitones: Int) =
        pitchRepository.getPitchById((pitch.id - semitones).coerceAtLeast(0))


    override val tuningsList: StateFlow<List<Pair<TuningSet, Instrument>>> get() = _tuningsList.asStateFlow()
    private val _tuningsList by lazy {
        MutableStateFlow(fakeTuningSets.map { tuning ->
            tuning to previewInstrument
        })
    }

    override val instrumentsAvailableList: StateFlow<List<Pair<Instrument, Boolean>>> get() = _instrumentsAvailableList.asStateFlow()
    private val _instrumentsAvailableList by lazy {
        MutableStateFlow(listOf(previewInstrument to true))
    }

    override val stringsCountAvailableList: StateFlow<List<Pair<Int, Boolean>>> get() = _stringsCountAvailableList.asStateFlow()
    private val _stringsCountAvailableList by lazy {
        MutableStateFlow(listOf(6 to true))
    }

    override fun updateTuningSet(tuningSet: TuningSet) {
        TODO("Not yet implemented")
    }

    override fun <T> updateTuningSet(tuningId: Int, tuningMap: Map<String, T>) {
        val tuning = fakeTuningSets[tuningId]
        val newTuning = tuning.copy(
            name = tuningMap["name"] as? String ?: tuning.name,
            isFavorite = tuningMap["isFavorite"] as? Boolean ?: tuning.isFavorite,
            pitches = tuningMap["pitches"] as? List<Pitch> ?: tuning.pitches,
        )

        fakeTuningSets = fakeTuningSets.map { if (it.tuningId == tuningId) newTuning else it }
        _tuningsList.update {
            it.map { t -> if (t.first.tuningId == tuningId) newTuning to t.second else t }
        }
    }

    override fun deleteTuning(tuningId: Int) {
        fakeTuningSets = fakeTuningSets.filter { it.tuningId != tuningId }
        _tuningsList.update {
            it.filter { t -> t.first.tuningId != tuningId }
        }
    }

    override fun updateInstrument(instrument: Instrument) {
        TODO()
    }

    override fun filterTunings(builder: TuningFilterBuilder.() -> Unit) {
        val filterBuilder = object : TuningFilterBuilder() {
            init {
                builder()
            }

            val filteredTunings = fakeTuningSets.filter { tuning ->
                filters.any { filter ->
                    when (filter) {
                        is TuningFilter.General -> when (filter) {
                            TuningFilter.General.ALL -> true
                            TuningFilter.General.FAVORITES -> tuning.isFavorite
                            TuningFilter.General.CUSTOM -> tuning.tuningId < 0
                        }

                        is TuningFilter.InstrumentId -> filter.id.contains(tuning.instrumentId)
                        is TuningFilter.CountStrings -> filter.count.contains(tuning.pitches.size)
                    }
                }
            }// .let { it.subList(startPaging, (startPaging + limit).coerceIn(startPaging..<it.size)) }
        }

        _tuningsList.update {
            filterBuilder.filteredTunings.map { tuning ->
                tuning to previewInstrument
            }
        }
    }
}