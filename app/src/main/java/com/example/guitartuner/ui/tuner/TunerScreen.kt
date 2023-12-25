package com.example.guitartuner.ui.tuner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guitartuner.ui.MainActivity
import com.example.guitartuner.ui.navigation.AppBarScreen
import com.example.guitartuner.ui.navigation.AppBarState
import com.example.guitartuner.ui.settings.SettingsViewModel
import com.example.guitartuner.ui.utils.AppNavigationInfo
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
//        Spacer(modifier = Modifier.height(32.dp))

        val screen = appBarState.currentAppBarScreen as? AppBarScreen.TunerAppBar
        LaunchedEffect(key1 = screen) {
            screen?.buttons?.onEach { button ->
                when (button) {
                    AppBarScreen.TunerAppBar.AppBarIcons.Settings -> navigateToSettingsTunings()
                }
            }?.launchIn(this)
        }

        // view models
        val vm = MainActivity.koinMainViewModel()!!
        val vmSettings = koinNavViewModel<SettingsViewModel>()

        vm.AttachLifecycleOwner(lifecycleOwner = LocalLifecycleOwner.current)

        val settingsState by vmSettings.state.collectAsStateWithLifecycle()
        val permissionState by vm.permissionState.collectAsStateWithLifecycle()
        val tunerState by vm.tunerState.collectAsStateWithLifecycle()
        val autoDetect by vm.autoMode.collectAsStateWithLifecycle()
        val selectedString by vm.selectedString.collectAsStateWithLifecycle()
        val buttonsState by vm.buttonsState.collectAsStateWithLifecycle()
        val tuningsState by vm.tuningsState.collectAsStateWithLifecycle()
        val currentTuningSet by vm.currentTuningSet.collectAsStateWithLifecycle()
        val currentlyTunedStrings by vm.currentlyTunedStrings.collectAsStateWithLifecycle()


        if (!permissionState.hasRequiredPermissions) {
            TunerPermissionScreen(
                canRequest = permissionState.canRequest,
                onRequestPermission = vm::onRequestPermission,
                onOpenPermissionSettings = onOpenPermissionSettings
            )
        } else {
            TunerMainScreen(
                expanded = false,
                contentType = appNavigationInfo.contentType,
                noteOffset = tunerState?.normalizedDeviation,
                isTuned = tunerState?.isTuned ?: false,
                tunings = tuningsState,
                currentTuningSet = currentTuningSet,
                buttonsUIState = buttonsState,
                selectedString = selectedString,
                tuned = currentlyTunedStrings,
                autoDetect = autoDetect,
                settings = settingsState,
                onSelectString = { vm.selectedString.value = it },
                onSelectTuning = { vm.selectedTuningId.value = it },
                onTuneUpString = vm::tuneUpString,
                onTuneDownString = vm::tuneDownString,
                onTuneUpTuning = vm::tuneUpTuning,
                onTuneDownTuning = vm::tuneDownTuning,
                onAutoChanged = { vm.autoMode.value = !autoDetect },
                onOpenTuningSelector = { navigateToSettingsTunings() },
            )
        }
    }
}