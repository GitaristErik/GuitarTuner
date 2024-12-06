package com.example.guitartuner.data.tuner

import android.util.Log
import com.example.guitartuner.data.db.AppDatabase
import com.example.guitartuner.data.db.model.TuningSetWithPitchesTable
import com.example.guitartuner.data.db.model.toTuningSet
import com.example.guitartuner.data.db.model.toTuningSetCrossRefTable
import com.example.guitartuner.data.db.model.toTuningSetTable
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.domain.entity.tuner.Alteration
import com.example.guitartuner.domain.entity.tuner.Instrument
import com.example.guitartuner.domain.entity.tuner.Note
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.entity.tuner.TuningSet
import com.example.guitartuner.domain.entity.tuner.previewInstrument
import com.example.guitartuner.domain.repository.tuner.PitchRepository
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository.TuningFilterBuilder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TuningSetsRepositoryImpl(
    private val pitchRepository: PitchRepository,
    private val coroutineScope: CoroutineScope,
    private val settingsManager: SettingsManager,
    private val database: AppDatabase,
) : TuningSetsRepository {

    override val currentInstrument: StateFlow<Instrument> =
        MutableStateFlow(previewInstrument).asStateFlow()

    override val favoritesTuningSets get() = _favoritesTuningSets
    private val _favoritesTuningSets = MutableStateFlow(listOf<TuningSet>())//.asStateFlow()
    private val _favoritesTuningDAO: Flow<List<TuningSetWithPitchesTable>> =
        database.tuningSetDAO.getFavouritesTunings()

    override val currentTuningSet get() = _currentTuningSet.asStateFlow()
    private val _currentTuningSet by lazy {
        MutableStateFlow(TuningSet(0, "", emptyList(), 0))
    }

    private val alteration = Alteration.SHARP
    private val initializationDeferred = CompletableDeferred<Unit>()

    init {
        initTuningSets()
        coroutineScope.launch(Dispatchers.IO) {
            initializationDeferred.await() // Suspend until DB is initialized
            launch { initFavoritesTuningSets() }
            initCurrentTuningSet()
        }
    }

    private fun initTuningSets() {
        coroutineScope.launch(Dispatchers.IO) {
            pitchRepository.purePitchesList.collectLatest { pitches ->
                if (pitches.isNotEmpty()) {
                    if (database.tuningSetDAO.count() == 0) {
                        // Initialize the database with default tuning sets
                        getDefaultTuningSets().forEach { updateTuningSetSuspend(it) }
                    }
                    // Signal that initialization is completed
                    initializationDeferred.complete(Unit)
                }
            }
        }
    }

    private suspend fun initCurrentTuningSet() {
        _currentTuningSet.value = getTuningSetById(settingsManager.lastTuningSetId) ?: return
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private suspend fun initFavoritesTuningSets() {
        _favoritesTuningDAO
            .distinctUntilChanged()
            .debounce(100)
            .transformLatest { list ->
                emit(list.toTuningSet(alteration))
            }.collectLatest {
                _favoritesTuningSets.value = it
            }
    }

    override fun selectTuning(tuningId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            launch { settingsManager.lastTuningSetId = tuningId }
            _currentTuningSet.value = getTuningSetById(tuningId) ?: return@launch
        }
    }

    private suspend fun getTuningSetById(tuningId: Int): TuningSet? {
        initializationDeferred.await() // Suspend until DB is initialized
        return database.tuningSetDAO
            .getTuningSetById(tuningId)
            .toTuningSet(alteration)
            .firstOrNull()
    }

    /*    init {
    *//*        coroutineScope.launch {
            delay(1000)
            initFavoritesTuningSetsCollector()
        }*//*
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
    }*/

    /*    private suspend fun initFavoritesTuningSetsCollector() {
            _tuningsList.collectLatest { tunings ->
                _favoritesTuningSets.value =
                    tunings.mapNotNull { if (it.first.isFavorite) it.first else null }
            }
        }*/

    private fun findTuning(pitches: List<Pitch>, instrumentId: Int) =
        database.tuningSetDAO
            .findTuningByPitchIdsAndInstrument(pitches.map { it.id }, instrumentId)
            ?.toTuningSet(alteration)
            ?.firstOrNull()


    private suspend fun updateTune(
        stringId: Int?, semitones: Int, tuneAction: suspend (Pitch, Int) -> Pitch?
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

    override suspend fun tuneUpTuning(semitones: Int) = updateTune(null, semitones, ::pitchTuneUp)

    override suspend fun tuneDownTuning(semitones: Int) =
        updateTune(null, semitones, ::pitchTuneDown)

    private suspend fun pitchTuneUp(pitch: Pitch, semitones: Int) =
        pitchRepository.getPitchById(pitch.id + semitones)

    private suspend fun pitchTuneDown(pitch: Pitch, semitones: Int) =
        pitchRepository.getPitchById((pitch.id - semitones).coerceAtLeast(0))


    override val tuningsList: StateFlow<List<Pair<TuningSet, Instrument>>> get() = _tuningsList
    private var _tuningsList = MutableStateFlow(emptyList<Pair<TuningSet, Instrument>>())


    override val instrumentsAvailableList: StateFlow<List<Pair<Instrument, Boolean>>> get() = _instrumentsAvailableList.asStateFlow()
    private val _instrumentsAvailableList by lazy {
        MutableStateFlow(listOf(previewInstrument to true))
    }

    override val stringsCountAvailableList: StateFlow<List<Pair<Int, Boolean>>> get() = _stringsCountAvailableList.asStateFlow()
    private val _stringsCountAvailableList by lazy {
        MutableStateFlow(listOf(6 to true))
    }

    private suspend fun updateTuningSetSuspend(tuningSet: TuningSet) {
        val id = database.tuningSetDAO.insertTuningSet(
            tuningSet.toTuningSetTable()
        )
        val insertedTuning = tuningSet.copy(tuningId = id.toInt())
        database.tuningSetDAO.insertTuningSetCrossRef(
            *insertedTuning.toTuningSetCrossRefTable().toTypedArray()
        )
    }

    override fun updateTuningSet(tuningSet: TuningSet) {
        coroutineScope.launch(Dispatchers.IO) {
            updateTuningSetSuspend(tuningSet)
        }
    }

    override fun <T> updateTuningSet(tuningId: Int, tuningMap: Map<String, T>) {
        coroutineScope.launch(Dispatchers.IO) {
            val tuning = getTuningSetById(tuningId) ?: return@launch
            val newTuning = tuning.copy(
                name = tuningMap["name"] as? String ?: tuning.name,
                isFavorite = tuningMap["isFavorite"] as? Boolean ?: tuning.isFavorite,
                pitches = tuningMap["pitches"] as? List<Pitch> ?: tuning.pitches,
                instrumentId = tuningMap["instrumentId"] as? Int ?: tuning.instrumentId,
            )
            updateTuningSet(newTuning)
        }
    }

    override fun deleteTuning(tuningId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            database.tuningSetDAO.deleteTuningSetById(tuningId)
        }
    }

    override fun updateInstrument(instrument: Instrument) {
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun filterTunings(builder: TuningFilterBuilder.() -> Unit) {
        object : TuningFilterBuilder() {
            init {
                builder()
            }

            val isAll = filters.firstOrNull { it == TuningFilter.General.ALL }?.let { true }

            val isFavorite =
                filters.firstOrNull { it == TuningFilter.General.FAVORITES }?.let { true }

            val instrumentIds =
                filters.filterIsInstance<TuningFilter.InstrumentId>().flatMap { it.id }

            val countString =
                filters.filterIsInstance<TuningFilter.CountStrings>().flatMap { it.count }

            val filteredTuningsFlow = database.tuningSetDAO.filterTunings(
                isFavorite = if (isAll == true) null else isFavorite,
                instrumentIds = instrumentIds,
//                    countString = countString,
                start = startPaging,
                limit = limit,
            ).transformLatest { list ->
                emit(list.toTuningSet(alteration).map { it to previewInstrument })
            }

        }.filteredTuningsFlow.let {
            coroutineScope.launch(Dispatchers.IO) {
                it.collectLatest {
                    _tuningsList.value = it
                }
            }
        }
    }


    // -----------

    private suspend fun getDefaultTuningSets() = mutableMapOf(
        // "Standard" - "E2, A2, D3, G3, B3, E4",
        "Fav Standard" to listOfNotNull(
            pitchRepository.findPitchByTone(tone = Tone(Note.E, 2)),
            pitchRepository.findPitchByTone(tone = Tone(Note.A, 2)),
            pitchRepository.findPitchByTone(tone = Tone(Note.D, 3)),
            pitchRepository.findPitchByTone(tone = Tone(Note.G, 3)),
            pitchRepository.findPitchByTone(tone = Tone(Note.B, 3)),
            pitchRepository.findPitchByTone(tone = Tone(Note.E, 4)),
        )
    ).run {
        putAll(
            linkedMapOf(
                // Half step down - "Eb2, Ab2, Db3, Gb3, Bb3, Eb4",
                "Fav Half step down" to listOfNotNull(
                    pitchRepository.findPitchByTone(tone = Tone(Note.E, 2, Alteration.FLAT)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.A, 2, Alteration.FLAT)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 3, Alteration.FLAT)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.G, 3, Alteration.FLAT)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.B, 3, Alteration.FLAT)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.E, 4, Alteration.FLAT)),
                ),
                // Whole step down - "D2, G2, C3, F3, A3, D4",
                "Whole step down" to listOfNotNull(
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.G, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.C, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.F, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.A, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 4)),
                ),
                // Drop D - "D2, A2, D3, G3, B3, E4",
                "Fav Drop D" to listOfNotNull(
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.A, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.G, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.B, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.E, 4)),
                ),
                // Drop C - "C2, G2, C3, F3, A3, D4",
                "Drop C" to listOfNotNull(
                    pitchRepository.findPitchByTone(tone = Tone(Note.C, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.G, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.C, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.F, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.A, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 4)),
                ),
                // Open D - "D2, A2, D3, F#3, A3, D4",
                "Open D" to listOfNotNull(
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.A, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.F, 3, Alteration.SHARP)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.A, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 4)),
                ),
                // Open G - "D2, G2, D3, G3, B3, D4",
                "Fav Open G" to listOfNotNull(
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.G, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.G, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.B, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 4)),
                ),
                // G Modal - "D2, G2, D3, G3, C4, D4",
                "G Modal" to listOfNotNull(
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.G, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.G, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.C, 4)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 4)),
                ),
                // All Fourths - "E2, A2, D3, G3, C4, F4",
                "All Fourths" to listOfNotNull(
                    pitchRepository.findPitchByTone(tone = Tone(Note.E, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.A, 2)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.D, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.G, 3)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.C, 4)),
                    pitchRepository.findPitchByTone(tone = Tone(Note.F, 4)),
                ),
            )
        )
        map { (name, pitches) ->
            TuningSet(
                tuningId = 0,
                name = name.removePrefix("Fav "),
                pitches = pitches,
                instrumentId = 1,
                isFavorite = name.commonPrefixWith("Fav ", true).isNotEmpty(),
            )
        }
    }
}