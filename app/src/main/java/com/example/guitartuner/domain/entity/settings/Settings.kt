package com.example.guitartuner.domain.entity.settings

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GridView
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import be.tarsos.dsp.pitch.PitchProcessor
import com.example.guitartuner.R
import com.example.guitartuner.domain.entity.tuner.Notation
import com.example.guitartuner.ui.settings.components.SettingsComponents.SelectOption

data class Settings(
    val generalNotation: Notation,
    val generalBaseFrequency: Int,

    val tunerUseAdvancedMode: Boolean,
    val tunerEnableNoiseSuppressor: Boolean,
    val tunerStringLayout: StringLayout,
    val tunerDisplayType: TunerDisplayType,
    val tunerPitchDetectionAlgorithm: TunerPitchDetectionAlgorithm,

    val soundPlaySoundInTune: Boolean,
    val soundPlaySoundOnSelect: Boolean,

    val themeUseFullBlackTheme: Boolean,
//    val accidental: AccidentalOption,
//    val deviationPrecision: DeviationPrecisionOption
) {

    enum class TunerPitchDetectionAlgorithm(
        @StringRes override val labelRes: Int,
        val algorithm: PitchProcessor.PitchEstimationAlgorithm
    ) : SelectOption.ResId<TunerPitchDetectionAlgorithm> {
        YIN(
            R.string.settings_detection_algorithm_yin,
            PitchProcessor.PitchEstimationAlgorithm.YIN
        ),
        FFT_YIN(
            R.string.settings_detection_algorithm_fft_yin,
            PitchProcessor.PitchEstimationAlgorithm.FFT_YIN
        ),
        MPM(
            R.string.settings_detection_algorithm_mpm,
            PitchProcessor.PitchEstimationAlgorithm.MPM
        ),
        AMDF(
            R.string.settings_detection_algorithm_amdf,
            PitchProcessor.PitchEstimationAlgorithm.AMDF
        ),
        DYWA(
            R.string.settings_detection_algorithm_dywa,
            PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET
        );

        companion object {
            @JvmStatic
            val titleRes = R.string.settings_detection_algorithm
        }
    }


    /** Enum representing the available layouts to display string controls. */
    @Immutable
    enum class StringLayout(
        override val labelRes: Int,
        override val iconVector: ImageVector
    ) : SelectOption.Icon<StringLayout>,
        SelectOption.ResId<StringLayout> {
        LIST(R.string.settings_tuner_layout_list, Icons.Filled.FormatListBulleted),
        GRID(R.string.settings_tuner_layout_grid, Icons.Filled.GridView),
    }

    /** Enum representing the available options for displaying tuning offset. */
    enum class TunerDisplayType(
        override val labelRes: Int,
        val multiplier: Double
    ) : SelectOption.ResId<TunerDisplayType> {
        SIMPLE(R.string.settings_display_type_simple, 10.0),
        CENTS(R.string.settings_display_type_cents, 1.0),
        SEMITONES(R.string.settings_display_type_semitones, 100.0),
    }


    companion object {
        @JvmStatic
        fun previewSettings(): Settings = Settings(
            generalNotation = Notation.Solfeggio,
            generalBaseFrequency = 440,
            tunerUseAdvancedMode = false,
            tunerEnableNoiseSuppressor = false,
            tunerStringLayout = StringLayout.GRID,
            tunerDisplayType = TunerDisplayType.SIMPLE,
            soundPlaySoundInTune = true,
            soundPlaySoundOnSelect = true,
            themeUseFullBlackTheme = true,
            tunerPitchDetectionAlgorithm = TunerPitchDetectionAlgorithm.YIN
        )
    }
}
