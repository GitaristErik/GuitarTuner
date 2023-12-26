package com.example.guitartuner.ui.settings

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.example.guitartuner.R
import com.example.guitartuner.ui.settings.components.TuningControls.SectionHeader
import com.example.guitartuner.ui.settings.components.TuningControls.TuningSettingsItem
import com.example.guitartuner.ui.theme.PreviewWrapper
import com.rohankhayech.android.util.ui.preview.ThemePreview
import org.koin.androidx.compose.navigation.koinNavViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsTuningsScreen() {
    val vmSettings = koinNavViewModel<SettingsViewModel>()
    val currentTuningState by vmSettings.currentTuningSet.collectAsState()

    LazyColumn {
        stickyHeader {
            SectionHeader(title = stringResource(id = R.string.settings_tunings_current_header))
        }

        Log.e("SettingsTuningsScreen", "currentTuningState: $currentTuningState")

        item {
            TuningSettingsItem(tuning = currentTuningState, onCustomSave = {

            })
        }
    }
}

@Composable
private fun SettingsTuningsScreenContent() {

}

@Composable
@ThemePreview
private fun SettingsTuningsScreenPreview() {
    PreviewWrapper {
        SettingsTuningsScreenContent()
    }
}