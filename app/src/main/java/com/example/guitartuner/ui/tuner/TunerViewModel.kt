package com.example.guitartuner.ui.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guitartuner.data.settings.SettingsManager
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.domain.entity.tuner.Tone
import com.example.guitartuner.domain.entity.tuner.Tuning
import com.example.guitartuner.domain.repository.tuner.PermissionManager
import com.example.guitartuner.domain.repository.tuner.PitchGenerationRepository
import com.example.guitartuner.domain.repository.tuner.TunerRepository
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository
import com.example.guitartuner.ui.model.TuneButtonsUIState
import com.example.guitartuner.ui.model.TuningUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TunerViewModel(
    private val permissionManager: PermissionManager,
    private val tunerRepository: TunerRepository,
    private val pitchGenerationRepository: PitchGenerationRepository,
    private val tuningSetsRepository: TuningSetsRepository,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    val permissionState by lazy { permissionManager.state }


    private val _tunerState by lazy { MutableStateFlow<Tuning?>(null) }
    val tunerState get() = _tunerState.asStateFlow()

    val buttonsState get() = _buttonsState
    private val _buttonsState by lazy {
        MutableStateFlow(
            TuneButtonsUIState(
                notation = settingsManager.generalNotation,
                instrument = tuningSetsRepository.currentInstrument.value,
                toneMap = mapPitchesToToneMap(
                    tuningSetsRepository.currentTuningSet.value.pitches
                )
            )
        )
    }

    val tuningsState get() = _tuningsState
    private val _tuningsState by lazy {
        MutableStateFlow(emptyMap<Int, TuningUIState>())
    }

    val currentTuningSet get() = _currentTuningSet.asStateFlow()
    private val _currentTuningSet by lazy {
        MutableStateFlow(TuningUIState("", ""))
    }

    val selectedTuningId = MutableStateFlow(0)
    val selectedString = MutableStateFlow<Int?>(null)
    val autoMode = MutableStateFlow(true)

    val currentlyTunedStrings get() = _currentlyTunedStrings.asStateFlow()
    private val _currentlyTunedStrings: MutableStateFlow<BooleanArray> by lazy {
        MutableStateFlow(BooleanArray(tuningSetsRepository.currentInstrument.value.countStrings))
    }


    init {
        onRequestPermission()
        initSettingsManagerObserver()
        initButtonsStateObserver()
        initTunerRepositoryObserver()
        initAutoModeObserver()
        initStringSelectorObserver()
        initTuningSelectorObserver()
        initFavoritesTuningSetsObserver()

//        tuningSetsRepository.favoritesTuningSets.launchIn(viewModelScope)
        tuningSetsRepository.currentInstrument.launchIn(viewModelScope)
        tuningSetsRepository.currentTuningSet.launchIn(viewModelScope)
    }

    private fun initFavoritesTuningSetsObserver() {
        viewModelScope.launch {
            tuningSetsRepository.favoritesTuningSets.collect { list ->
                _tuningsState.value = list.mapIndexed { index, tuning ->
                    index to TuningUIState(
                        tuningName = tuning.name,
                        notesList = tuning.pitches.joinToString(", ") { it.tone.toString() }
                    )
                }.toMap()
            }
        }
    }

    private fun initSettingsManagerObserver() {
        viewModelScope.launch {
            settingsManager.state.collect { settings ->
                _buttonsState.update {
                    it.copy(notation = settings.generalNotation)
                }
            }
        }
    }

    private fun mapPitchesToToneMap(pitches: List<Pitch>) =
        pitches.mapIndexed { index, pitch ->
            index to pitch.tone
        }.toMap()

    private fun initButtonsStateObserver() {
        viewModelScope.launch {
            tuningSetsRepository.currentTuningSet.collect {
                _buttonsState.update { state ->
                    state.copy(toneMap = mapPitchesToToneMap(it.pitches))
                }
            }
        }
    }

    private fun initTunerRepositoryObserver() {
        viewModelScope.launch {
            tunerRepository.state
                .collectLatest {
                    _tunerState.value = it

                    getStringFromTone(
                        it?.closestPitch?.tone ?: return@collectLatest
                    )?.let { index ->
                        if (settingsManager.soundPlaySoundInTune && it.isTuned) {
                            playInTuneSound(index)
                        }
                        _currentlyTunedStrings.update { tunedStrings ->
                            tunedStrings.apply { set(index, it.isTuned) }
                        }
                    }
                }
        }
    }

    private fun initTuningSelectorObserver() {
        viewModelScope.launch {
            selectedTuningId.collect {
                tuningSetsRepository.selectTuning(it)
            }
        }
        viewModelScope.launch {
            tuningSetsRepository.currentTuningSet.collect { tuning ->
                _currentTuningSet.value = TuningUIState(
                    tuningName = tuning.name,
                    notesList = tuning.pitches.joinToString(", ") { it.tone.toString() }
                )
            }
        }
    }

    private fun initAutoModeObserver() {
        viewModelScope.launch {
            autoMode.collect {
                tunerRepository.autoMode = it
            }
        }
    }

    private fun getStringFromTone(tone: Tone): Int? =
        buttonsState.value.toneMap.entries.firstOrNull { it.value == tone }?.key

    private fun getStringTone(stringId: Int): Tone = buttonsState.value.toneMap[stringId]!!
    private fun initStringSelectorObserver() {
        viewModelScope.launch {
            selectedString.collect {
                if (it != null) {
                    tunerRepository.selectTone(getStringTone(it))
                    if (settingsManager.state.value.soundPlaySoundOnSelect) {
                        playStringSelectSound()
                    }
                    autoMode.value = false
                }
            }
        }
    }

    fun onRequestPermission() {
        viewModelScope.launch {
            requestPermissions()
        }
    }

    private suspend fun requestPermissions() {
        permissionManager.requestPermissions()
    }

    private fun playStringSelectSound() {
        pitchGenerationRepository.playStringSelectSound(
            selectedString.value ?: return
        )
    }

    private fun playInTuneSound(stringId: Int) {
        pitchGenerationRepository.playInTuneSound(
            stringId
        )
    }

    fun tuneUpString(stringId: Int) {
        tuningSetsRepository.tuneUpString(stringId)
    }

    fun tuneDownString(stringId: Int) {
        tuningSetsRepository.tuneDownString(stringId)
    }

    fun tuneUpTuning() {
        tuningSetsRepository.tuneUpTuning()
    }

    fun tuneDownTuning() {
        tuningSetsRepository.tuneDownTuning()
    }
}