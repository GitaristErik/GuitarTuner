package com.example.guitartuner.ui.tuner

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guitartuner.ui.MainActivity
import com.example.guitartuner.ui.navigation.AppBarScreen
import com.example.guitartuner.ui.navigation.AppBarState
import com.example.guitartuner.ui.settings.SettingsViewModel
import com.example.guitartuner.ui.tuner.components.createPreviewButtonsUIState
import com.example.guitartuner.ui.tuner.components.previewTuningState
import com.example.guitartuner.ui.utils.AppNavigationInfo
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.scope.AndroidScopeComponent
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

        val vm = MainActivity.koinMainViewModel()!!
        val vmSettings = koinNavViewModel<SettingsViewModel>()
        val settingsState by vmSettings.state.collectAsStateWithLifecycle()
        val permissionState by vm.state.collectAsStateWithLifecycle()

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
                noteOffset = remember { mutableDoubleStateOf(1.3) },
                tunings = remember { mutableStateOf(previewTuningState) },
                selectedTuningId = 1,
                buttonsUIState = createPreviewButtonsUIState(settingsState.generalNotation),
                selectedString = 1,
                tuned = BooleanArray(6) { it == 4 },
                autoDetect = true,
                settings = settingsState,
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
}

fun Context.findActivity(): AndroidScopeComponent {
    var context = this
    while (context is ContextWrapper) {
        if (context is AndroidScopeComponent) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}
