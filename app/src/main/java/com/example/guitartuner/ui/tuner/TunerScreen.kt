package com.example.guitartuner.ui.tuner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guitartuner.ui.model.TunerEvent
import com.example.guitartuner.ui.navigation.AppBarScreen
import com.example.guitartuner.ui.navigation.AppBarState
import com.example.guitartuner.ui.settings.SettingsViewModel
import com.example.guitartuner.ui.utils.AppNavigationInfo
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.compose.navigation.koinNavViewModel

@Composable
fun TunerScreen(
    modifier: Modifier = Modifier,
    appNavigationInfo: AppNavigationInfo,
    appBarState: AppBarState,
    navigateToSettingsTunings: () -> Unit,
    onOpenPermissionSettings: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        val screen = appBarState.currentAppBarScreen as? AppBarScreen.TunerAppBar
        LaunchedEffect(key1 = screen) {
            screen?.buttons?.onEach { button ->
                when (button) {
                    AppBarScreen.TunerAppBar.AppBarIcons.Settings -> navigateToSettingsTunings()
                }
            }?.launchIn(this)
        }

        val vm = koinViewModel<TunerViewModel>()
        val vmSettings = koinNavViewModel<SettingsViewModel>()

        vm.AttachLifecycleOwner(lifecycleOwner = LocalLifecycleOwner.current)

        val settingsState by vmSettings.state.collectAsStateWithLifecycle()
        val uiState by vm.uiState.collectAsStateWithLifecycle()

        if (!uiState.permission.hasRequiredPermissions) {
            TunerPermissionScreen(
                canRequest = uiState.permission.canRequest,
                onRequestPermission = { vm.onEvent(TunerEvent.RequestPermission) },
                onOpenPermissionSettings = onOpenPermissionSettings
            )
        } else {
            val buttons = uiState.buttons
            if (buttons != null) {
                TunerMainScreen(
                    expanded = false,
                    contentType = appNavigationInfo.contentType,
                    noteOffset = uiState.noteOffset,
                    isTuned = uiState.isTuned,
                    tunings = uiState.tunings,
                    currentTuningSet = uiState.currentTuning,
                    buttonsUIState = buttons,
                    selectedString = uiState.selectedString,
                    tuned = uiState.tunedStrings,
                    autoDetect = uiState.autoDetect,
                    settings = settingsState,
                    onSelectString = { vm.onEvent(TunerEvent.SelectString(it)) },
                    onSelectTuning = { vm.onEvent(TunerEvent.SelectTuning(it)) },
                    onTuneUpString = { vm.onEvent(TunerEvent.TuneUpString(it)) },
                    onTuneDownString = { vm.onEvent(TunerEvent.TuneDownString(it)) },
                    onTuneUpTuning = { vm.onEvent(TunerEvent.TuneUpTuning) },
                    onTuneDownTuning = { vm.onEvent(TunerEvent.TuneDownTuning) },
                    onAutoChanged = { vm.onEvent(TunerEvent.SetAutoDetect(it)) },
                    onOpenTuningSelector = { navigateToSettingsTunings() },
                )
            }
        }
    }
}
