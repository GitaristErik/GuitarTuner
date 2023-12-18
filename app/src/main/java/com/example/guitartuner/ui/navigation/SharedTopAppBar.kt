package com.example.guitartuner.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.guitartuner.ui.navigation.AppBarScreenRoute.Companion.getAppBarScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Stable
class AppBarState(
    navController: NavController,
    scope: CoroutineScope,
) {
    init {
        navController.currentBackStackEntryFlow
            .distinctUntilChanged()
            .onEach { backStackEntry ->
                val route = backStackEntry.destination.route
                currentAppBarScreen = route?.getAppBarScreen()
            }
            .launchIn(scope)
    }

    var currentAppBarScreen by mutableStateOf<AppBarScreen?>(null)
        private set

    val isVisible: Boolean
        @Composable get() = currentAppBarScreen?.isAppBarVisible == true

    val navigationIcon: ImageVector?
        @Composable get() = currentAppBarScreen?.navigationIcon

    val navigationIconContentDescription: String?
        @Composable get() = currentAppBarScreen?.navigationIconContentDescription

    val onNavigationIconClick: (() -> Unit)?
        @Composable get() = currentAppBarScreen?.onNavigationIconClick

    val title @Composable get() = currentAppBarScreen?.title

    val actions @Composable get() = currentAppBarScreen?.actions
}

@Composable
fun rememberAppBarState(
    navController: NavController,
    scope: CoroutineScope = rememberCoroutineScope(),
) = remember {
    AppBarState(
        navController,
        scope,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedTopAppBar(
    appBarState: AppBarState,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        colors = topAppBarColors(),
        navigationIcon = {
            val icon = appBarState.navigationIcon
            val callback = appBarState.onNavigationIconClick

            if (icon != null) {
                IconButton(onClick = { callback?.invoke() }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = appBarState.navigationIconContentDescription
                    )
                }
            }
        },
        title = appBarState.title ?: {},
        actions = appBarState.actions ?: {},
        modifier = modifier
        /*        title = {
            val title = appBarState.title
            if (title.isNotEmpty()) {
                Text(
                    text = title
                )
            }
        },
        actions = {
            val items = appBarState.actions
            if (items.isNotEmpty()) {
                ActionsMenu(
                    items = items,
                    isOpen = menuExpanded,
                    onToggleOverflow = { menuExpanded = !menuExpanded },
                    maxVisibleItems = 3,
                )
            }
        },*/
    )
}