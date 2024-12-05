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
import com.example.guitartuner.ui.navigation.AppBarScreen.MainAppBar
import com.example.guitartuner.ui.navigation.AppBarScreen.MainAppBar.TitleType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class AppBarRoute(val route: String) {
/*    GaugeAppBar(AppRoutScreen.Gauge.route) {
        override val screen: AppBarScreen by lazy {
            MainAppBar(this, TitleType.TitleResId(R.string.tab_gauge))
        }
    },
    MetronomeAppBar(AppRoutScreen.Metronome.route) {
        override val screen: AppBarScreen by lazy {
            MainAppBar(this, TitleType.TitleResId(R.string.tab_metronome))
        }
    },*/
    SettingsAppBar(AppRoutScreen.SettingsAll.route) {
        override val screen: AppBarScreen by lazy {
            MainAppBar(this, TitleType.TitleResId(R.string.tab_settings))
        }
    },

    TunerAppBar(AppRoutScreen.Tuner.route) {
        override val screen: AppBarScreen by lazy { AppBarScreen.TunerAppBar() }
    },
    SettingsTuningsAppBar(AppRoutScreen.SettingsTunings.route) {
        override val screen: AppBarScreen by lazy { AppBarScreen.SettingsAppBar() }
    };

    abstract val screen: AppBarScreen

    companion object {

        @JvmStatic
        fun getScreenFromRoute(route: String?): AppBarScreen? =
            entries.find { it.route == route }?.screen

        @JvmStatic
        fun String.getAppBarScreen(): AppBarScreen? = getScreenFromRoute(this)
    }
}

sealed class AppBarScreen {
    abstract val route: AppBarRoute
    abstract val title: @Composable () -> Unit // String
    open val actions: @Composable RowScope.() -> Unit = {} // List<ActionMenuItem>

    open val isAppBarVisible: Boolean = true
    open val navigationIcon: ImageVector? = null
    open val navigationIconContentDescription: String? = null
    open val onNavigationIconClick: (() -> Unit)? = null

    abstract class AppBarScreenWithButtons<E : Enum<E>> : AppBarScreen() {
        protected val _buttons = MutableSharedFlow<E>(extraBufferCapacity = 1)
        val buttons: Flow<E> = _buttons.asSharedFlow()
    }

    class MainAppBar(
        override val route: AppBarRoute,
        private val stringTitle: TitleType
    ) : AppBarScreen() {
        override val title = @Composable {
            Text(
                when (stringTitle) {
                    is TitleType.TitleString -> stringTitle.title
                    is TitleType.TitleResId -> stringResource(stringTitle.title)
                }
            )
        }

        init {
            println("MainAppBar init | title = $title | route = $route")
        }

        sealed interface TitleType {

            @JvmInline
            value class TitleString(val title: String) : TitleType

            @JvmInline
            value class TitleResId(val title: Int) : TitleType
        }
    }

    class TunerAppBar : AppBarScreenWithButtons<TunerAppBar.AppBarIcons>() {
        enum class AppBarIcons { Settings }

        override val route: AppBarRoute = AppBarRoute.TunerAppBar
        override val title = @Composable { Text(stringResource(R.string.tab_tuner)) }
        override val actions: @Composable (RowScope.() -> Unit) = {
            IconButton(onClick = { _buttons.tryEmit(AppBarIcons.Settings) }) {
                Icon(Icons.Default.Tune, stringResource(R.string.tuner_settings))
            }
        }
    }

    class SettingsAppBar : AppBarScreenWithButtons<SettingsAppBar.AppBarIcons>() {
        enum class AppBarIcons { NavigationIcon }

        init {
            println("SettingsAppBar init")
        }

        override val route: AppBarRoute = AppBarRoute.SettingsTuningsAppBar

        override val title = @Composable { Text(text = stringResource(R.string.tuner_settings)) }
        override val navigationIcon: ImageVector = Icons.Default.ArrowBack
        override val navigationIconContentDescription: String? = null
        override val onNavigationIconClick: () -> Unit = {
            _buttons.tryEmit(AppBarIcons.NavigationIcon)
        }
    }
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
