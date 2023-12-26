package com.example.guitartuner.ui.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.guitartuner.ui.navigation.AppBarScreen
import com.example.guitartuner.ui.navigation.AppBarState
import com.example.guitartuner.ui.navigation.AppRoutRoot
import com.example.guitartuner.ui.navigation.AppRoutScreen
import com.example.guitartuner.ui.settings.SettingsScreen
import com.example.guitartuner.ui.settings.SettingsTuningsScreen
import com.example.guitartuner.ui.tuner.TunerScreen
import com.example.guitartuner.ui.utils.AppNavigationInfo
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    appNavigationInfo: AppNavigationInfo,
    appBarState: AppBarState
) {
    val context = LocalContext.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppRoutRoot.Tuner.route,
    ) {
        navigation(
            route = AppRoutRoot.Tuner.route,
            startDestination = AppRoutScreen.Tuner.route,
        ) {
            composable(AppRoutScreen.Tuner.route) {
                TunerScreen(
                    appNavigationInfo = appNavigationInfo,
                    navigateToSettingsTunings = {
                        navController.navigate(AppRoutScreen.SettingsTunings.route)
                    },
                    appBarState = appBarState,
                    onOpenPermissionSettings = { context.navigateToPermissionSettings() },
                )
            }
        }

        navigation(
            route = AppRoutRoot.Metronome.route,
            startDestination = AppRoutScreen.Metronome.route,
        ) {
            composable(AppRoutScreen.Metronome.route) {
                EmptyComingSoon()
            }
        }

        navigation(
            route = AppRoutRoot.Gauge.route,
            startDestination = AppRoutScreen.Gauge.route,
        ) {
            composable(AppRoutScreen.Gauge.route) {
                EmptyComingSoon()
            }
        }

        navigation(
            route = AppRoutRoot.Settings.route,
            startDestination = AppRoutScreen.SettingsAll.route,
        ) {
            composable(AppRoutScreen.SettingsAll.route) {
                SettingsScreen(
                    onClickAbout = {},
                    onClickTunings = { navController.navigate(AppRoutScreen.SettingsTunings.route) },
                )
            }

            composable(AppRoutScreen.SettingsTunings.route) {
                LaunchedEffect(key1 = Unit) {
                    (appBarState.currentAppBarScreen as? AppBarScreen.SettingsAppBar)?.buttons?.onEach { button ->
                        when (button) {
                            AppBarScreen.SettingsAppBar.AppBarIcons.NavigationIcon -> navController.popBackStack()
                        }
                    }?.launchIn(this)
                }
                SettingsTuningsScreen()
            }
        }
    }
}

/** Opens the permission settings screen in the device settings. */
private fun Context.navigateToPermissionSettings() {
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
    )
}