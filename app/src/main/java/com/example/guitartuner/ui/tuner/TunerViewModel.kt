package com.example.guitartuner.ui.tuner

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.repository.tuner.PermissionManager
import com.example.guitartuner.domain.repository.tuner.PitchGenerationRepository
import com.example.guitartuner.domain.repository.tuner.TunerRepository
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository
import com.example.guitartuner.ui.model.TuneButtonsUIState
import com.example.guitartuner.ui.model.TunerEvent
import com.example.guitartuner.ui.model.TunerUiState
import com.example.guitartuner.ui.model.TuningUIState
import com.example.guitartuner.ui.utils.ObserveLifecycleEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TunerViewModel(
    private val permissionManager: PermissionManager,
    private val tunerRepository: TunerRepository,
    private val pitchGenerationRepository: PitchGenerationRepository,
    private val tuningSetsRepository: TuningSetsRepository,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TunerUiState())
    val uiState: StateFlow<TunerUiState> = _uiState.asStateFlow()

    init {
        onEvent(TunerEvent.RequestPermission)
        observePermission()
        observeSettingsAndInstrument()
        observeTuner()
        observeFavorites()
        observeCurrentTuning()
    }

    fun onEvent(event: TunerEvent) {
        when (event) {
            TunerEvent.RequestPermission -> viewModelScope.launch { permissionManager.requestPermissions() }
            is TunerEvent.SelectString -> selectString(event.stringId)
            is TunerEvent.SelectTuning -> {
                _uiState.update { it.copy(selectedTuningId = event.tuningId) }
                tuningSetsRepository.selectTuning(event.tuningId)
            }
            TunerEvent.ToggleAutoDetect -> setAutoDetect(!_uiState.value.autoDetect)
            is TunerEvent.SetAutoDetect -> setAutoDetect(event.enabled)
            is TunerEvent.TuneUpString -> viewModelScope.launch(Dispatchers.IO) {
                tuningSetsRepository.tuneUpString(event.stringId)
            }
            is TunerEvent.TuneDownString -> viewModelScope.launch(Dispatchers.IO) {
                tuningSetsRepository.tuneDownString(event.stringId)
            }
            TunerEvent.TuneUpTuning -> viewModelScope.launch(Dispatchers.IO) {
                tuningSetsRepository.tuneUpTuning()
            }
            TunerEvent.TuneDownTuning -> viewModelScope.launch(Dispatchers.IO) {
                tuningSetsRepository.tuneDownTuning()
            }
            TunerEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun setAutoDetect(enabled: Boolean) {
        tunerRepository.autoMode = enabled
        _uiState.update {
            it.copy(
                autoDetect = enabled,
                selectedString = if (enabled) null else it.selectedString,
            )
        }
    }

    private fun selectString(stringId: Int) {
        val tone = _uiState.value.buttons?.toneMap?.get(stringId) ?: return
        tunerRepository.selectTone(tone)
        if (settingsManager.state.value.soundPlaySoundOnSelect) {
            pitchGenerationRepository.playStringSelectSound(stringId)
        }
        tunerRepository.autoMode = false
        _uiState.update {
            it.copy(selectedString = stringId, autoDetect = false)
        }
    }

    private fun observePermission() {
        viewModelScope.launch {
            permissionManager.state.collectLatest { permission ->
                _uiState.update { it.copy(permission = permission) }
            }
        }
    }

    private fun observeSettingsAndInstrument() {
        viewModelScope.launch {
            settingsManager.state.collectLatest { settings ->
                _uiState.update { state ->
                    val buttons = state.buttons?.copy(notation = settings.generalNotation)
                        ?: TuneButtonsUIState(
                            notation = settings.generalNotation,
                            instrument = tuningSetsRepository.currentInstrument.value,
                            toneMap = mapPitchesToToneMap(
                                tuningSetsRepository.currentTuningSet.value.pitches
                            )
                        )
                    state.copy(buttons = buttons)
                }
            }
        }
        viewModelScope.launch {
            tuningSetsRepository.currentInstrument.collectLatest { instrument ->
                _uiState.update { state ->
                    val size = instrument.countStrings
                    val tuned = List(size) { index ->
                        state.tunedStrings.getOrElse(index) { false }
                    }
                    state.copy(
                        buttons = (state.buttons ?: TuneButtonsUIState(
                            notation = settingsManager.generalNotation,
                            instrument = instrument,
                            toneMap = emptyMap(),
                        )).copy(instrument = instrument),
                        tunedStrings = tuned,
                    )
                }
            }
        }
        viewModelScope.launch {
            tuningSetsRepository.currentTuningSet.collectLatest { tuning ->
                _uiState.update { state ->
                    state.copy(
                        buttons = (state.buttons ?: TuneButtonsUIState(
                            notation = settingsManager.generalNotation,
                            instrument = tuningSetsRepository.currentInstrument.value,
                            toneMap = emptyMap(),
                        )).copy(toneMap = mapPitchesToToneMap(tuning.pitches)),
                        currentTuning = TuningUIState(
                            tuningId = tuning.tuningId,
                            tuningName = tuning.name,
                            notesList = tuning.pitches.joinToString(", ") { it.tone.toString() }
                        ),
                        selectedTuningId = if (tuning.tuningId > 0) tuning.tuningId else state.selectedTuningId,
                        // Reset tuned flags when tuning changes
                        tunedStrings = List(
                            tuningSetsRepository.currentInstrument.value.countStrings
                        ) { false },
                    )
                }
            }
        }
    }

    private fun observeTuner() {
        viewModelScope.launch {
            tunerRepository.state.collectLatest { tuning ->
                _uiState.update { state ->
                    var tunedStrings = state.tunedStrings
                    val tone = tuning?.closestPitch?.tone
                    val index = tone?.let { getStringFromTone(it) }
                    if (index != null && tuning != null) {
                        if (settingsManager.soundPlaySoundInTune && tuning.isTuned) {
                            pitchGenerationRepository.playInTuneSound(index)
                        }
                        tunedStrings = tunedStrings.toMutableList().also { list ->
                            if (index in list.indices) {
                                list[index] = tuning.isTuned
                            }
                        }
                    }
                    state.copy(
                        noteOffset = tuning?.normalizedDeviation,
                        isTuned = tuning?.isTuned ?: false,
                        tunedStrings = tunedStrings,
                    )
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            tuningSetsRepository.favoritesTuningSets.collect { list ->
                _uiState.update { state ->
                    state.copy(
                        tunings = list.map { tuning ->
                            TuningUIState(
                                tuningId = tuning.tuningId,
                                tuningName = tuning.name,
                                notesList = tuning.pitches.joinToString(", ") { it.tone.toString() }
                            )
                        }
                    )
                }
            }
        }
    }

    private fun observeCurrentTuning() {
        // current tuning already handled in observeSettingsAndInstrument
    }

    private fun mapPitchesToToneMap(pitches: List<Pitch>): Map<Int, Tone> =
        pitches.mapIndexed { index, pitch -> index to pitch.tone }.toMap()

    private fun getStringFromTone(tone: Tone): Int? =
        _uiState.value.buttons?.toneMap?.entries?.firstOrNull { it.value == tone }?.key

    @Composable
    fun AttachLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        tunerRepository.ObserveLifecycleEvents(lifecycleOwner = lifecycleOwner)
        pitchGenerationRepository.ObserveLifecycleEvents(lifecycleOwner = lifecycleOwner)
    }
}
