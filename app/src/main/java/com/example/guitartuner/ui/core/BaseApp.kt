package com.example.guitartuner.ui.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.example.guitartuner.ui.navigation.AppBarScreen
import com.example.guitartuner.ui.navigation.AppBarState
import com.example.guitartuner.ui.navigation.AppBottomNavigationBar
import com.example.guitartuner.ui.navigation.AppNavigationRail
import com.example.guitartuner.ui.navigation.AppRoutRoot
import com.example.guitartuner.ui.navigation.AppRoutScreen
import com.example.guitartuner.ui.navigation.ModalNavigationDrawerContent
import com.example.guitartuner.ui.navigation.PermanentNavigationDrawerContent
import com.example.guitartuner.ui.navigation.SharedTopAppBar
import com.example.guitartuner.ui.navigation.TopLevelDestination
import com.example.guitartuner.ui.navigation.currentRouteAsState
import com.example.guitartuner.ui.navigation.currentScreenAsState
import com.example.guitartuner.ui.navigation.navigateToRouteRoot
import com.example.guitartuner.ui.navigation.rememberAppBarState
import com.example.guitartuner.ui.settings.SettingsScreen
import com.example.guitartuner.ui.settings.SettingsViewModel
import com.example.guitartuner.ui.tuner.TunerScreen
import com.example.guitartuner.ui.utils.AppNavigationInfo
import com.example.guitartuner.ui.utils.ContentType
import com.example.guitartuner.ui.utils.DevicePosture
import com.example.guitartuner.ui.utils.NavigationContentPosition
import com.example.guitartuner.ui.utils.NavigationType
import com.example.guitartuner.ui.utils.isBookPosture
import com.example.guitartuner.ui.utils.isSeparating
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.navigation.koinNavViewModel

@Composable
fun BaseApp(
    windowSize: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
) {
    /**
     * This will help us select type of navigation and content type depending on window size and
     * fold state of the device.
     */
    val navigationType: NavigationType
    val contentType: ContentType

    /**
     * We are using display's folding features to map the device postures a fold is in.
     * In the state of folding device If it's half fold in BookPosture we want to avoid content
     * at the crease/hinge
     */
    val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

    val foldingDevicePosture = when {
        isBookPosture(foldingFeature) -> DevicePosture.BookPosture(foldingFeature.bounds)

        isSeparating(foldingFeature) -> DevicePosture.Separating(
            foldingFeature.bounds, foldingFeature.orientation
        )

        else -> DevicePosture.NormalPosture
    }

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = NavigationType.BOTTOM_NAVIGATION
            contentType = ContentType.SINGLE_PANE
        }

        WindowWidthSizeClass.Medium -> {
            navigationType = NavigationType.NAVIGATION_RAIL
            contentType = if (foldingDevicePosture != DevicePosture.NormalPosture) {
                ContentType.DUAL_PANE
            } else {
                ContentType.SINGLE_PANE
            }
        }

        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                NavigationType.NAVIGATION_RAIL
            } else {
                if (windowSize.heightSizeClass == WindowHeightSizeClass.Compact) {
                    NavigationType.NAVIGATION_RAIL
                } else {
                    NavigationType.PERMANENT_NAVIGATION_DRAWER
                }
            }
            contentType = ContentType.DUAL_PANE
        }

        else -> {
            navigationType = NavigationType.BOTTOM_NAVIGATION
            contentType = ContentType.SINGLE_PANE
        }
    }

    /**
     * Content inside Navigation Rail/Drawer can also be positioned at top, bottom or center for
     * ergonomics and reachability depending upon the height of the device.
     */
    val navigationContentPosition = when (windowSize.heightSizeClass) {
        WindowHeightSizeClass.Compact -> {
            NavigationContentPosition.TOP
        }

        WindowHeightSizeClass.Medium, WindowHeightSizeClass.Expanded -> {
            NavigationContentPosition.CENTER
        }

        else -> {
            NavigationContentPosition.TOP
        }
    }

    AppNavigationWrapper(
        AppNavigationInfo(
            devicePosture = foldingDevicePosture,
            navigationType = navigationType,
            contentType = contentType,
            displayFeatures = displayFeatures,
            navigationContentPosition = navigationContentPosition,
        )
    )
}

@Composable
private fun AppNavigationWrapper(
    appNavigationInfo: AppNavigationInfo,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val selectedDestination by navController.currentScreenAsState()
    val currentRoute by navController.currentRouteAsState()
    val navigateTo: (TopLevelDestination) -> Unit = { navController.navigateToRouteRoot(it.route) }

    if (appNavigationInfo.navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
        // TODO check on custom width of PermanentNavigationDrawer: b/232495216
        PermanentNavigationDrawer(drawerContent = {
            PermanentNavigationDrawerContent(
                selectedDestination = selectedDestination,
                navigationContentPosition = appNavigationInfo.navigationContentPosition,
                navigateToTopLevelDestination = navigateTo,
            )
        }) {
            AppContent(
                appNavigationInfo = appNavigationInfo,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigateTo,
            )
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                ModalNavigationDrawerContent(selectedDestination = selectedDestination,
                    navigationContentPosition = appNavigationInfo.navigationContentPosition,
                    navigateToTopLevelDestination = navigateTo,
                    onDrawerClicked = {
                        scope.launch { drawerState.close() }
                    })
            },
            drawerState = drawerState,
        ) {
            AppContent(
                appNavigationInfo = appNavigationInfo,
                navController = navController,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigateTo,
                onDrawerClicked = {
                    scope.launch { drawerState.open() }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    appNavigationInfo: AppNavigationInfo,
    navController: NavHostController,
    selectedDestination: AppRoutRoot,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = appNavigationInfo.navigationType == NavigationType.NAVIGATION_RAIL
        ) {
            AppNavigationRail(
                selectedDestination = selectedDestination,
                navigationContentPosition = appNavigationInfo.navigationContentPosition,
                navigateToTopLevelDestination = navigateToTopLevelDestination,
                onDrawerClicked = onDrawerClicked,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            val snackbarHostState = remember { SnackbarHostState() }
            val appBarState = rememberAppBarState(navController)

            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
                if (appBarState.isVisible) {
                    SharedTopAppBar(
                        appBarState = appBarState,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }, bottomBar = {
                AnimatedVisibility(
                    visible = appNavigationInfo.navigationType == NavigationType.BOTTOM_NAVIGATION
                ) {
                    AppBottomNavigationBar(
                        selectedDestination = selectedDestination,
                        navigateToTopLevelDestination = navigateToTopLevelDestination
                    )
                }
            }) { innerPaddingModifier ->
                AppNavHost(
                    modifier = Modifier
                        .weight(1f)
                        .padding(innerPaddingModifier),
                    navController = navController,
                    appNavigationInfo = appNavigationInfo,
                    appBarState = appBarState
                )
            }
        }
    }
}

@Composable
private fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    appNavigationInfo: AppNavigationInfo,
    appBarState: AppBarState
) {
//    var data by remember { mutableStateOf(Settings.previewSettings()) }
    val vmSettings = koinNavViewModel<SettingsViewModel>()
    val data by vmSettings.state.collectAsState()

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
                    appBarState = appBarState
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
//                SettingsScreen(TunerPreferences(), {}, {}, {}, {}, {}, {})

                SettingsScreen(
                    settings = data,
                    updateSettings = { vmSettings.updateSettings(it) },
//                    settings = vmSettings.state.collectAsState(scope.coroutineContext).value,
//                    updateSettings = { vmSettings.updateSettings(it) },
                    onClickAbout = {},
                    onClickTunings = { navController.navigate(AppRoutScreen.SettingsTunings.route) },
                )
            }

            composable(AppRoutScreen.SettingsTunings.route) {
                EmptyComingSoon()
                LaunchedEffect(key1 = Unit) {
                    (appBarState.currentAppBarScreen as? AppBarScreen.SettingsAppBar)?.buttons?.onEach { button ->
                        when (button) {
                            AppBarScreen.SettingsAppBar.AppBarIcons.NavigationIcon -> navController.popBackStack()
                        }
                    }?.launchIn(this)
                }
            }
        }
    }
}
