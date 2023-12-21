package com.example.guitartuner.domain.entity.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GridView
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.guitartuner.R
import com.example.guitartuner.domain.entity.tuner.Notation
import com.example.guitartuner.ui.settings.components.SettingsComponents

data class Settings(
    val generalNotation: Notation,

    val tunerUseAdvancedMode: Boolean,
    val tunerEnableNoiseSuppressor: Boolean,
    val tunerStringLayout: StringLayout,
    val tunerDisplayType: TunerDisplayType,

    val soundPlaySoundInTune: Boolean,
    val soundPlaySoundOnSelect: Boolean,

    val themeUseFullBlackTheme: Boolean,
//    val accidental: AccidentalOption,
//    val pitchDetectionAlgorithm: PitchDetectionAlgorithmOption,
//    val deviationPrecision: DeviationPrecisionOption
) {

    /** Enum representing the available layouts to display string controls. */
    @Immutable
    enum class StringLayout(
        override val labelRes: Int,
        override val iconVector: ImageVector
    ) : SettingsComponents.SelectOption.Icon<StringLayout>,
        SettingsComponents.SelectOption.ResId<StringLayout> {
        LIST(R.string.settings_tuner_layout_list, Icons.Filled.FormatListBulleted),
        GRID(R.string.settings_tuner_layout_grid, Icons.Filled.GridView),
    }

    /** Enum representing the available options for displaying tuning offset. */
    enum class TunerDisplayType(
        override val labelRes: Int,
        val multiplier: Int
    ) : SettingsComponents.SelectOption.ResId<TunerDisplayType> {
        SIMPLE(R.string.settings_display_type_simple, 10),
        CENTS(R.string.settings_display_type_cents, 1),
        SEMITONES(R.string.settings_display_type_semitones, 100),
    }


    companion object {
        @JvmStatic
        fun previewSettings(): Settings = Settings(
            generalNotation = Notation.Solfeggio,
            tunerUseAdvancedMode = false,
            tunerEnableNoiseSuppressor = false,
            tunerStringLayout = StringLayout.GRID,
            tunerDisplayType = TunerDisplayType.SIMPLE,
            soundPlaySoundInTune = true,
            soundPlaySoundOnSelect = true,
            themeUseFullBlackTheme = true,
        )
    }
}
