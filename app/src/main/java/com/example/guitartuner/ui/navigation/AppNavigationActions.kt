package com.example.guitartuner.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.SettingsVoice
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.SettingsVoice
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.guitartuner.R

enum class AppRoutRoot(val route: String) {
    Tuner("root_tuner"),
    Gauge("root_gauge"),
    Metronome("root_metronome"),
    Settings("root_settings"),
}

enum class AppRoutScreen(val route: String) {
    Tuner("tuner"),
    Gauge("gauge"),
    Metronome("metronome"),
    SettingsAll("settings:all"),
    SettingsTunings("settings:tunings"),
}

enum class TopLevelDestination(
    val route: AppRoutRoot,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
) {

    Tuner(
        route = AppRoutRoot.Tuner,
        selectedIcon = Icons.Filled.SettingsVoice,
        unselectedIcon = Icons.Outlined.SettingsVoice,
        iconTextId = R.string.tab_tuner
    ),
    Metronome(
        route = AppRoutRoot.Metronome,
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer,
        iconTextId = R.string.tab_metronome
    ),
    Gauge(
        route = AppRoutRoot.Gauge,
        selectedIcon = Icons.Filled.Calculate,
        unselectedIcon = Icons.Outlined.Calculate,
        iconTextId = R.string.tab_gauge
    ),
    Settings(
        route = AppRoutRoot.Settings,
        selectedIcon = Icons.Filled.Tune,
        unselectedIcon = Icons.Outlined.Tune,
        iconTextId = R.string.tab_settings
    )

}

@Stable
@Composable
fun NavController.currentScreenAsState(): State<AppRoutRoot> {
    val selectedItem = remember { mutableStateOf<AppRoutRoot>(AppRoutRoot.Tuner) }
    DisposableEffect(key1 = this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == AppRoutRoot.Tuner.route } -> {
                    selectedItem.value = AppRoutRoot.Tuner
                }

                destination.hierarchy.any { it.route == AppRoutRoot.Gauge.route } -> {
                    selectedItem.value = AppRoutRoot.Gauge
                }

                destination.hierarchy.any { it.route == AppRoutRoot.Metronome.route } -> {
                    selectedItem.value = AppRoutRoot.Metronome
                }

                destination.hierarchy.any { it.route == AppRoutRoot.Settings.route } -> {
                    selectedItem.value = AppRoutRoot.Settings
                }
            }

        }
        addOnDestinationChangedListener(listener)
        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}

@Stable
@Composable
fun NavController.currentRouteAsState(): State<String?> {
    val selectedItem = remember { mutableStateOf<String?>(null) }
    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem.value = destination.route
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}

fun NavController.navigateToRouteRoot(appRoutRoot: AppRoutRoot) {
    navigate(appRoutRoot.route) {
        launchSingleTop = true
        restoreState = true
//        popUpTo(graph.findStartDestination().id) { saveState = true }
        graph.parent?.let { popUpTo(it.findStartDestination().id) { saveState = true } }
    }
}