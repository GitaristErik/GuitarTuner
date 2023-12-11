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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.guitartuner.R

object ReplyRoute {
    const val TUNER = "Tuner"
    const val GAUGE = "Gauge"
    const val METRONOME = "Metronome"
    const val SETTINGS = "Settings"
}

data class ReplyTopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

class ReplyNavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: ReplyTopLevelDestination) {
        navController.navigate(destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    ReplyTopLevelDestination(
        route = ReplyRoute.TUNER,
        selectedIcon = Icons.Filled.SettingsVoice,
        unselectedIcon = Icons.Outlined.SettingsVoice,
        iconTextId = R.string.tab_tuner
    ),
    ReplyTopLevelDestination(
        route = ReplyRoute.GAUGE,
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer,
        iconTextId = R.string.tab_metronome
    ),
    ReplyTopLevelDestination(
        route = ReplyRoute.METRONOME,
        selectedIcon = Icons.Filled.Calculate,
        unselectedIcon = Icons.Outlined.Calculate,
        iconTextId = R.string.tab_gauge
    ),
    ReplyTopLevelDestination(
        route = ReplyRoute.SETTINGS,
        selectedIcon = Icons.Filled.Tune,
        unselectedIcon = Icons.Outlined.Tune,
        iconTextId = R.string.tab_settings
    )
)
