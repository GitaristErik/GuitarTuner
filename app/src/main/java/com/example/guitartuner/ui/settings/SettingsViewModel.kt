package com.example.guitartuner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.domain.entity.settings.Settings
import com.example.guitartuner.domain.entity.tuner.Instrument
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.TuningSet
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository.TuningFilterBuilder.TuningFilter
import com.example.guitartuner.ui.model.FilterBoxUIState
import com.example.guitartuner.ui.model.TuningSettingsUIState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager,
    private val tuningsRepository: TuningSetsRepository
) : ViewModel() {

    val state by lazy { settingsManager.state }

    fun updateSettings(settings: Settings) {
        settingsManager.settings = settings
    }


    val currentTuningSet get() = _currentTuningSet.asStateFlow()
    private val _currentTuningSet by lazy {
        MutableStateFlow<TuningSettingsUIState?>(null)
    }

    private val _filtersSelectedState by lazy { MutableStateFlow(TuningFilterSelectedState()) }

    private data class TuningFilterSelectedState(
        val general: Set<TuningFilter.General> = setOf(TuningFilter.General.ALL),
        val instrument: TuningFilter.InstrumentId = TuningFilter.InstrumentId(setOf()),
        val strings: TuningFilter.CountStrings = TuningFilter.CountStrings(setOf())
    )

    val filtersInstrumentState get() = _filtersInstrumentState.asStateFlow()
    private val _filtersInstrumentState by lazy {
        MutableStateFlow<List<FilterBoxUIState<Int>>?>(null)
    }

    val filtersStringsState get() = _filtersStringsState.asStateFlow()
    private val _filtersStringsState by lazy {
        MutableStateFlow<List<FilterBoxUIState<Int>>?>(null)
    }


    init {
        initCollectorCurrentTuning()
        initCollectorFiltersInstrument()
        initCollectorFiltersStrings()
        initObserverFiltersSelectedState()
    }

    @OptIn(FlowPreview::class)
    private fun initObserverFiltersSelectedState() {
        viewModelScope.launch {
            _filtersSelectedState
                .debounce(DELAY_FILTERS_BEFORE_REQUEST)
                .collectLatest {
                tuningsRepository.filterTunings {
                    filter(it.general)
                    filter(it.instrument)
                    filter(it.strings)
                }
            }
        }
    }

    private fun initCollectorFiltersInstrument() {
        viewModelScope.launch {
            tuningsRepository.instrumentsAvailableList.collectLatest {
                _filtersInstrumentState.value = it.map { (instrument, isAvailable) ->
                    FilterBoxUIState(
                        key = instrument.instrumentId.toString(),
                        value = instrument.instrumentId,
                        text = instrument.name,
                        isEnabled = isAvailable,
                    )
                }
            }
        }
    }

    private fun initCollectorFiltersStrings() {
        viewModelScope.launch {
            tuningsRepository.stringsCountAvailableList.collectLatest {
                _filtersStringsState.value = it.map { (count, isAvailable) ->
                    FilterBoxUIState(
                        key = count.toString(),
                        value = count,
                        text = count.toString(),
                        isEnabled = isAvailable,
                    )
                }
            }
        }
    }

    private fun initCollectorCurrentTuning() {
        viewModelScope.launch {
            tuningsRepository.currentTuningSet.combine(
                tuningsRepository.currentInstrument
            ) { tuning, instrument ->
                TuningSettingsUIState(
                    tuningId = tuning.tuningId,
                    instrumentName = instrument.name,
                    instrumentDetails = instrument.countStrings.toString(),
                    tuningName = tuning.name,
                    notesList = tuning.pitches.mapToNotesList(),
                    isFavorite = tuning.isFavorite,
                    isCustom = tuning.tuningId < 0,
                )
            }.collectLatest { _currentTuningSet.value = it }
        }
    }

    private fun List<Pitch>.mapToNotesList(): String =
        map { it.tone }.joinToString(", ")


    fun updateTuning(tuning: TuningSet) =
        tuningsRepository.updateTuningSet(tuning)

    fun updateInstrument(instrument: Instrument) =
        tuningsRepository.updateInstrument(instrument)

    fun toggleFilterGeneral(
        filter: TuningFilter.General,
        isSelected: Boolean
    ) {
        _filtersSelectedState.update {
            it.copy(general = if (isSelected) it.general + filter else it.general - filter)
        }
    }

    fun toggleFilterInstrument(filter: Int, selected: Boolean) {
        _filtersSelectedState.value.instrument.id.contains(filter).let { se ->
            val addOrRemove = if (se && !selected) false else if (!se && selected) true else return

            _filtersSelectedState.update {
                it.copy(
                    instrument = TuningFilter.InstrumentId(
                        if (addOrRemove) it.instrument.id + filter
                        else it.instrument.id - filter
                    )
                )
            }
        }
    }

    fun toggleFilterStrings(filter: Int, selected: Boolean) {
        _filtersSelectedState.value.strings.count.contains(filter).let { se ->
            val addOrRemove = if (se && !selected) false else if (!se && selected) true else return

            _filtersSelectedState.update {
                it.copy(
                    strings = TuningFilter.CountStrings(
                        if (addOrRemove) it.strings.count + filter
                        else it.strings.count - filter
                    )
                )
            }
        }
    }

    companion object {
        private const val DELAY_FILTERS_BEFORE_REQUEST = 300L
    }
}