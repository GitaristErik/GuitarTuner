package com.example.guitartuner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.domain.entity.settings.Settings
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository
import com.example.guitartuner.ui.model.TuningSettingsUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager,
    private val tuningsRepository: TuningSetsRepository
) : ViewModel() {

    val state by lazy { settingsManager.state }

    val currentTuningSet get() = _currentTuningSet.asStateFlow()
    private val _currentTuningSet by lazy {
        MutableStateFlow(TuningSettingsUIState(0, "", "", "", ""))
    }

    init {
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
            }.stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                TuningSettingsUIState(0, "", "", "", "")
            ).collectLatest { _currentTuningSet.value = it }
        }
    }

    private fun List<Pitch>.mapToNotesList(): String =
        map { it.tone }.joinToString(", ")

    fun updateSettings(settings: Settings) {
        settingsManager.settings = settings
    }
}