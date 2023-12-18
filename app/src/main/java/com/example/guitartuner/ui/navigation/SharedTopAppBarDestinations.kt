package com.example.guitartuner.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.guitartuner.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface AppBarScreen {
    val route: AppBarScreenRoute
    val isAppBarVisible: Boolean
    val navigationIcon: ImageVector?
    val navigationIconContentDescription: String?
    val onNavigationIconClick: (() -> Unit)?
    val title: @Composable () -> Unit // String
    val actions: @Composable RowScope.() -> Unit // List<ActionMenuItem>
}

enum class AppBarScreenRoute(val route: String) {
    HomeRouteAppBar(AppRoute.TUNER) {
        override val screen: AppBarScreen by lazy { TunerAppBar() }
    },
    SettingsRouteAppBar(AppRoute.SETTINGS) {
        override val screen: AppBarScreen by lazy { SettingsAppBar() }
    },/*
        ManyOptionsRouteAppBar("manyOptions") {
            override val screen: AppBarScreen by lazy { ManyOptionsAppBarScreen() }
        },
        NoAppBarRouteAppBar("noAppBarRoute") {
            override val screen: AppBarScreen by lazy { NoAppBar() }
        }*/;

    abstract val screen: AppBarScreen

    companion object {
        @JvmStatic
        fun getScreenFromRoute(route: String?): AppBarScreen? =
            entries.find { it.route == route }?.screen

        @JvmStatic
        fun String.getAppBarScreen(): AppBarScreen? = getScreenFromRoute(this)
    }
}


class TunerAppBar : AppBarScreen {
    enum class AppBarIcons {
        Settings
    }

    private val _buttons = MutableSharedFlow<AppBarIcons>(extraBufferCapacity = 1)
    val buttons: Flow<AppBarIcons> = _buttons.asSharedFlow()

    override val route: AppBarScreenRoute = AppBarScreenRoute.HomeRouteAppBar
    override val isAppBarVisible: Boolean = true
    override val navigationIcon: ImageVector? = null
    override val onNavigationIconClick: (() -> Unit)? = null
    override val navigationIconContentDescription: String? = null
    override val title = @Composable { Text(stringResource(R.string.tab_tuner)) }
    override val actions: @Composable (RowScope.() -> Unit) = {
        IconButton(onClick = { _buttons.tryEmit(AppBarIcons.Settings) }) {
            Icon(Icons.Default.Tune, stringResource(R.string.configure_tuning))
        }
    }/*    override val actions: List<ActionMenuItem> = listOf(
            ActionMenuItem.IconMenuItem.AlwaysShown(
                title = stringResource(R.string.configure_tuning),
                onClick = { _buttons.tryEmit(AppBarIcons.Settings) },
                icon = Icons.Filled.Tune,
                contentDescription = null,
            )
        )*/
}


class SettingsAppBar : AppBarScreen {
    override val route: AppBarScreenRoute = AppBarScreenRoute.SettingsRouteAppBar
    override val isAppBarVisible: Boolean = true
    override val navigationIcon: ImageVector = Icons.Default.ArrowBack
    override val onNavigationIconClick: () -> Unit = {
        _buttons.tryEmit(AppBarIcons.NavigationIcon)
    }
    override val navigationIconContentDescription: String? = null
    override val title = @Composable { Text(text = "Settings") }
    override val actions: @Composable (RowScope.() -> Unit) = {}

    enum class AppBarIcons {
        NavigationIcon
    }

    private val _buttons = MutableSharedFlow<AppBarIcons>(extraBufferCapacity = 1)
    val buttons: Flow<AppBarIcons> = _buttons.asSharedFlow()
}


/*

class Settings : AppBarScreen {
    override val route: AppBarScreenRoute = AppBarScreenRoute.SettingsRouteAppBar
    override val isAppBarVisible: Boolean = true
    override val navigationIcon: ImageVector = Icons.Default.ArrowBack
    override val onNavigationIconClick: () -> Unit = {
        _buttons.tryEmit(AppBarIcons.NavigationIcon)
    }
    override val navigationIconContentDescription: String? = null
    override val title: String = "Settings"
    override val actions: List<ActionMenuItem> = emptyList()

    enum class AppBarIcons {
        NavigationIcon
    }

    private val _buttons = MutableSharedFlow<AppBarIcons>(extraBufferCapacity = 1)
    val buttons: Flow<AppBarIcons> = _buttons.asSharedFlow()
}

class NoAppBar : AppBarScreen {
    override val route: AppBarScreenRoute = AppBarScreenRoute.NoAppBarRouteAppBar
    override val isAppBarVisible: Boolean = false
    override val navigationIcon: ImageVector? = null
    override val onNavigationIconClick: (() -> Unit)? = null
    override val navigationIconContentDescription: String? = null
    override val title: String = ""
    override val actions: List<ActionMenuItem> = emptyList()
}


class ManyOptionsAppBarScreen : AppBarScreen {
    override val route: AppBarScreenRoute = AppBarScreenRoute.ManyOptionsRouteAppBar
    override val isAppBarVisible: Boolean = true
    override val navigationIcon: ImageVector = Icons.Default.ArrowBack
    override val onNavigationIconClick: () -> Unit = {
        _buttons.tryEmit(AppBarIcons.NavigationIcon)
    }
    override val navigationIconContentDescription: String? = null
    override val title: String = "Many Options"

    // 1
    private var _favoriteIcon by mutableStateOf<ImageVector>(Icons.Default.FavoriteBorder)

    // 2
    override val actions: List<ActionMenuItem> by derivedStateOf {
        listOf(
            ActionMenuItem.IconMenuItem.AlwaysShown(
                title = "Search",
                onClick = {
                    _buttons.tryEmit(AppBarIcons.Search)
                },
                icon = Icons.Filled.Search,
                contentDescription = null,
            ),
            ActionMenuItem.IconMenuItem.AlwaysShown(
                title = "Favorite",
                onClick = {
                    _buttons.tryEmit(AppBarIcons.Favorite)
                },
                // 3
                icon = _favoriteIcon,
                contentDescription = null,
            ),
            ActionMenuItem.IconMenuItem.ShownIfRoom(
                title = "Star",
                onClick = {
                    _buttons.tryEmit(AppBarIcons.Star)
                },
                icon = Icons.Filled.Star,
                contentDescription = null,
            ),
            ActionMenuItem.IconMenuItem.ShownIfRoom(
                title = "Refresh",
                onClick = {
                    _buttons.tryEmit(AppBarIcons.Refresh)
                },
                icon = Icons.Filled.Refresh,
                contentDescription = null,
            ),
            ActionMenuItem.NeverShown(
                title = "Settings",
                onClick = {
                    _buttons.tryEmit(AppBarIcons.Settings)
                },
            ),
            ActionMenuItem.NeverShown(
                title = "About",
                onClick = {
                    _buttons.tryEmit(AppBarIcons.About)
                },
            ),
        )
    }

    fun setFavoriteIcon(icon: ImageVector) {
        _favoriteIcon = icon
    }

    enum class AppBarIcons {
        NavigationIcon,
        Search,
        Favorite,
        Star,
        Refresh,
        Settings,
        About,
    }

    private val _buttons = MutableSharedFlow<AppBarIcons>(extraBufferCapacity = 1)
    val buttons: Flow<AppBarIcons> = _buttons.asSharedFlow()
}*/
