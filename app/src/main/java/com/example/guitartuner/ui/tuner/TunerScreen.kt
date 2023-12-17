package com.example.guitartuner.ui.tuner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.guitartuner.R
import com.example.guitartuner.domain.entity.settings.TunerPreferences
import com.example.guitartuner.ui.navigation.TopAppBarProvider
import com.example.guitartuner.ui.tuner.components.previewButtonsUIState
import com.example.guitartuner.ui.tuner.components.previewTuningState
import com.example.guitartuner.ui.utils.AppNavigationInfo

@Composable
fun TunerScreen(
    modifier: Modifier = Modifier,
    appNavigationInfo: AppNavigationInfo,
    navigateToSettingsTunings: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
//        Spacer(modifier = Modifier.height(32.dp))

        TopAppBarProvider.defaultActions = {
            IconButton(
                onClick = {}//navigateToSettingsTunings
            ) {
                Icon(Icons.Default.Tune, stringResource(R.string.configure_tuning))
            }
        }

        TunerMainScreen(
            expanded = false,
            contentType = appNavigationInfo.contentType,
            noteOffset = remember { mutableDoubleStateOf(1.3) },
            tunings = remember { mutableStateOf(previewTuningState) },
            selectedTuningId = 1,
            buttonsUIState = previewButtonsUIState,
            selectedString = 1,
            tuned = BooleanArray(6) { it == 4 },
            autoDetect = true,
            prefs = TunerPreferences(),
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )
    }
}

