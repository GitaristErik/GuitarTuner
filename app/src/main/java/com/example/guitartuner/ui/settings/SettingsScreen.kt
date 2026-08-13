package com.example.guitartuner.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guitartuner.R
import com.example.guitartuner.domain.entity.settings.Settings
import com.example.guitartuner.domain.entity.tuner.Notation
import com.example.guitartuner.ui.settings.components.SectionLabel
import com.example.guitartuner.ui.settings.components.SettingsComponents
import com.example.guitartuner.ui.theme.PreviewWrapper
import com.rohankhayech.android.util.ui.preview.ThemePreview
import org.koin.androidx.compose.navigation.koinNavViewModel


@Composable
fun SettingsScreen(
    onClickTunings: () -> Unit,
    onClickAbout: () -> Unit,
    onClickLanguage: () -> Unit,
) {
    val vmSettings = koinNavViewModel<SettingsViewModel>()
    val settings by vmSettings.state.collectAsStateWithLifecycle()
    val updateSettings = { it: Settings -> vmSettings.updateSettings(it) }

    SettingsScreenBody(
        settings = settings,
        updateSettings = updateSettings,
        onClickTunings = onClickTunings,
        onClickAbout = onClickAbout,
        onClickLanguage = onClickLanguage
    )
}

@Composable
private fun SettingsScreenBody(
    settings: Settings,
    updateSettings: (Settings) -> Unit,
    onClickTunings: () -> Unit,
    onClickAbout: () -> Unit,
    onClickLanguage: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top)
    ) {

        // General
        SectionLabel(title = stringResource(R.string.settings_general))
        SettingsComponents.PreferenceNumberInput(
            title = stringResource(R.string.settings_general_base_frequency),
            subtitle = stringResource(R.string.settings_general_base_frequency_desc),
            initValue = settings.generalBaseFrequency,
            valueDescription = stringResource(R.string.settings_general_base_frequency_value_desc),
            onValueChange = {
                updateSettings(
                    settings.copy(generalBaseFrequency = Settings.clampBaseFrequency(it))
                )
            },
            max = Settings.MAX_BASE_FREQUENCY,
            min = Settings.MIN_BASE_FREQUENCY,
        )
        SettingsComponents.PreferenceSelector(
            title = stringResource(R.string.settings_general_notation),
            selected = settings.generalNotation,
            options = Notation.entries.toTypedArray(),
            onSelected = { updateSettings(settings.copy(generalNotation = it)) }
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        // ---------------


        // Tuner preferences
        SectionLabel(title = stringResource(R.string.settings_tuner))
        SettingsComponents.PreferenceActionLink(
            title = stringResource(R.string.settings_tuner_tunings),
            subtitle = stringResource(R.string.settings_tuner_tunings_desc),
            icon = Icons.Outlined.List,
            onClick = onClickTunings
        )
        SettingsComponents.PreferenceSwitch(
            title = stringResource(R.string.settings_noise_suppressor),
            subtitle = stringResource(R.string.settings_noise_suppressor_desc),
            checked = settings.tunerEnableNoiseSuppressor,
            onChanged = { updateSettings(settings.copy(tunerEnableNoiseSuppressor = !settings.tunerEnableNoiseSuppressor)) }
        )
        SettingsComponents.PreferenceNumberInput(
            title = stringResource(R.string.settings_tuner_deviation),
            subtitle = stringResource(R.string.settings_tuner_deviation_desc),
            initValue = settings.tunerMinDeviation,
            valueDescription = stringResource(R.string.settings_tuner_deviation_desc_value),
            onValueChange = {
                updateSettings(
                    settings.copy(tunerMinDeviation = Settings.clampDeviation(it))
                )
            },
            max = Settings.MAX_DEVIATION,
            min = Settings.MIN_DEVIATION,
        )
        SettingsComponents.PreferenceSelector(
            title = stringResource(R.string.settings_tuner_layout),
            selected = settings.tunerStringLayout,
            options = Settings.StringLayout.entries.toTypedArray(),
            onSelected = { updateSettings(settings.copy(tunerStringLayout = it)) },
            icons = mapOf(
                Settings.StringLayout.LIST to Icons.AutoMirrored.Filled.FormatListBulleted,
                Settings.StringLayout.GRID to Icons.Filled.GridView,
            ),
        )
        SettingsComponents.PreferenceSelector(
            title = stringResource(R.string.settings_tuner_display),
            selected = settings.tunerDisplayType,
            options = Settings.TunerDisplayType.entries.toTypedArray(),
            onSelected = { updateSettings(settings.copy(tunerDisplayType = it)) }
        )
        SettingsComponents.PreferenceSelector(
            title = stringResource(R.string.settings_detection_algorithm),
            selected = settings.tunerPitchDetectionAlgorithm,
            options = Settings.TunerPitchDetectionAlgorithm.entries.toTypedArray(),
            onSelected = { updateSettings(settings.copy(tunerPitchDetectionAlgorithm = it)) }
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        // ---------------


        // Sound preferences
        SectionLabel(title = stringResource(R.string.settings_sound))

        SettingsComponents.PreferenceSwitch(title = stringResource(R.string.settings_enable_string_select_sound),
            subtitle = stringResource(R.string.settings_enable_string_select_sound_desc),
            checked = settings.soundPlaySoundOnSelect,
            onChanged = { updateSettings(settings.copy(soundPlaySoundOnSelect = !settings.soundPlaySoundOnSelect)) }
        )
        SettingsComponents.PreferenceSwitch(
            title = stringResource(R.string.settings_enable_in_tune_sound),
            subtitle = stringResource(R.string.settings_enable_in_tune_sound_desc),
            checked = settings.soundPlaySoundInTune,
            onChanged = { updateSettings(settings.copy(soundPlaySoundInTune = !settings.soundPlaySoundInTune)) }
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

        // ---------------
        // Other
        SectionLabel(stringResource(R.string.other))
        SettingsComponents.PreferenceActionLink(
            title = stringResource(R.string.lang),
            subtitle = stringResource(R.string.lang_message),
            icon = Icons.Outlined.Language,
            onClick = onClickLanguage
        )
        SettingsComponents.PreferenceActionLink(
            title = stringResource(R.string.about),
            subtitle = stringResource(R.string.app_name),
            icon = Icons.Outlined.Info,
            onClick = onClickAbout
        )

        // ---------------
    }
}

// Preview
@ThemePreview
@Composable
private fun Preview() {
    PreviewWrapper {
        SettingsScreenBody(
            settings = Settings(
                soundPlaySoundInTune = true,
                soundPlaySoundOnSelect = true,
                tunerEnableNoiseSuppressor = false,
                generalNotation = Notation.Solfeggio,
                generalBaseFrequency = 440,
                tunerStringLayout = Settings.StringLayout.GRID,
                tunerDisplayType = Settings.TunerDisplayType.SIMPLE,
                tunerPitchDetectionAlgorithm = Settings.TunerPitchDetectionAlgorithm.YIN
            ),
            updateSettings = {},
            onClickTunings = {},
            onClickAbout = {},
            onClickLanguage = {},
        )
    }
}
