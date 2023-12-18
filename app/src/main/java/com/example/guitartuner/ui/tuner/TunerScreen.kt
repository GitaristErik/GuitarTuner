package com.example.guitartuner.ui.tuner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.guitartuner.domain.entity.settings.TunerPreferences
import com.example.guitartuner.ui.navigation.AppBarScreen
import com.example.guitartuner.ui.navigation.AppBarState
import com.example.guitartuner.ui.tuner.components.previewButtonsUIState
import com.example.guitartuner.ui.tuner.components.previewTuningState
import com.example.guitartuner.ui.utils.AppNavigationInfo
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun TunerScreen(
    modifier: Modifier = Modifier,
    appNavigationInfo: AppNavigationInfo,
    appBarState: AppBarState,
    navigateToSettingsTunings: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
//        Spacer(modifier = Modifier.height(32.dp))

        val screen = appBarState.currentAppBarScreen as? AppBarScreen.TunerAppBar
        LaunchedEffect(key1 = screen) {
            screen?.buttons?.onEach { button ->
                when (button) {
                    AppBarScreen.TunerAppBar.AppBarIcons.Settings -> navigateToSettingsTunings()
                }
            }?.launchIn(this)
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

