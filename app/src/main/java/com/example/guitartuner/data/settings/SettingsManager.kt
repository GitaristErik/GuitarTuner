package com.example.guitartuner.data.settings

import cafe.adriel.satchel.SatchelStorage
import cafe.adriel.satchel.ktx.value
import com.example.guitartuner.domain.entity.settings.Settings
import com.example.guitartuner.domain.entity.tuner.Notation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class SettingsManager(
    storage: SatchelStorage,
    private val scope: CoroutineScope
) {

    private val _state by lazy { MutableStateFlow(settings) }
    val state by lazy { _state.asStateFlow() }


    private val _baseFrequency = MutableSharedFlow<Int>()
    @OptIn(FlowPreview::class)
    val baseFrequency
        get() = _baseFrequency
            .distinctUntilChanged()
            .debounce(750)


    var lastTuningSetId by storage.value(
        key = "last_tuning_set_id",
        defaultValue = 1
    )

    var generalNotation by storage.value(
        key = "general_notation",
        defaultValue = Notation.English
    )
    var generalBaseFrequency by storage.value(
        key = "general_base_frequency",
        defaultValue = 440
    )
/*    var tunerUseAdvancedMode by storage.value(
        key = "tuner_advanced_mode",
        defaultValue = true
    )*/
    var tunerEnableNoiseSuppressor by storage.value(
        key = "tuner_enable_noise_suppressor",
        defaultValue = true
    )
    var tunerMinDeviation by storage.value(
        key = "tuner_min_deviation",
        defaultValue = 10
    )
    var tunerStringLayout by storage.value(
        key = "tuner_string_layout",
        defaultValue = Settings.StringLayout.GRID
    )
    var tunerDisplayType by storage.value(
        key = "tuner_display_type",
        defaultValue = Settings.TunerDisplayType.SIMPLE
    )
    var tunerPitchDetectionAlgorithm by storage.value(
        key = "tuner_pitch_detection_algorithm",
        defaultValue = Settings.TunerPitchDetectionAlgorithm.YIN
    )
    var soundPlaySoundOnSelect by storage.value(
        key = "sound_play_sound_on_select",
        defaultValue = true
    )
    var soundPlaySoundInTune by storage.value(
        key = "sound_play_sound_in_tune",
        defaultValue = true
    )
/*    var themeUseFullBlackTheme by storage.value(
        key = "theme_use_full_black_theme",
        defaultValue = true
    )*/

    var settings: Settings
        get() = Settings(
            generalNotation = generalNotation,
            generalBaseFrequency = generalBaseFrequency,

//            tunerUseAdvancedMode = tunerUseAdvancedMode,
            tunerEnableNoiseSuppressor = tunerEnableNoiseSuppressor,
            tunerMinDeviation = tunerMinDeviation,
            tunerStringLayout = tunerStringLayout,
            tunerDisplayType = tunerDisplayType,
            tunerPitchDetectionAlgorithm = tunerPitchDetectionAlgorithm,

            soundPlaySoundOnSelect = soundPlaySoundOnSelect,
            soundPlaySoundInTune = soundPlaySoundInTune,

//            themeUseFullBlackTheme = themeUseFullBlackTheme,
        )
        set(value) {
            generalNotation = value.generalNotation
            if (value.generalBaseFrequency != generalBaseFrequency) scope.launch {
                _baseFrequency.emit(value.generalBaseFrequency)
            }
            generalBaseFrequency = value.generalBaseFrequency

//            tunerUseAdvancedMode = value.tunerUseAdvancedMode
            tunerEnableNoiseSuppressor = value.tunerEnableNoiseSuppressor
            tunerMinDeviation = value.tunerMinDeviation
            tunerStringLayout = value.tunerStringLayout
            tunerDisplayType = value.tunerDisplayType
            tunerPitchDetectionAlgorithm = value.tunerPitchDetectionAlgorithm

            soundPlaySoundOnSelect = value.soundPlaySoundOnSelect
            soundPlaySoundInTune = value.soundPlaySoundInTune

//            themeUseFullBlackTheme = value.themeUseFullBlackTheme
        }

    init {
        storage.addListener(scope) {
            _state.value = settings
        }
    }
}
