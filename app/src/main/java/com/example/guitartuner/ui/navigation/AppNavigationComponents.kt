package com.example.guitartuner.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import com.example.guitartuner.R
import com.example.guitartuner.ui.utils.NavigationContentPosition

@Composable
fun AppNavigationRail(
    selectedDestination: AppRoutRoot,
    navigationContentPosition: NavigationContentPosition,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {},
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(
            modifier = Modifier.layoutId(LayoutType.HEADER),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            NavigationRailItem(selected = false, onClick = onDrawerClicked, icon = {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(id = R.string.navigation_drawer)
                )
            })
            Spacer(Modifier.height(8.dp)) // NavigationRailHeaderPadding
            Spacer(Modifier.height(4.dp)) // NavigationRailVerticalPadding
        }

        Column(
            modifier = Modifier.layoutId(LayoutType.CONTENT),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TopLevelDestination.entries.forEach { appDestination ->
                val selected = selectedDestination == appDestination.route
                NavigationRailItem(selected = selected,
                    onClick = { navigateToTopLevelDestination(appDestination) },
                    icon = {
                        Icon(
                            imageVector = if (selected) appDestination.selectedIcon else appDestination.unselectedIcon,
                            contentDescription = stringResource(
                                id = appDestination.iconTextId
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    selectedDestination: AppRoutRoot,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        windowInsets = NavigationBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
    ) {
        TopLevelDestination.entries.forEach { appDestination ->
            val selected = selectedDestination == appDestination.route
            NavigationBarItem(
                selected = selected,
                onClick = { navigateToTopLevelDestination(appDestination) },
                label = {
                    Text(
                        text = stringResource(id = appDestination.iconTextId),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        softWrap = false
                    )
                },
                icon = {
                    Icon(
                        imageVector = if (selected) appDestination.selectedIcon else appDestination.unselectedIcon,
                        contentDescription = stringResource(id = appDestination.iconTextId)
                    )
                }
            )
        }
    }
}

@Composable
fun PermanentNavigationDrawerContent(
    selectedDestination: AppRoutRoot,
    navigationContentPosition: NavigationContentPosition,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit,
) {
    PermanentDrawerSheet(
        modifier = Modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
    ) {
        // TODO remove custom nav drawer content positioning when NavDrawer component supports it. ticket : b/232495216
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(16.dp), content = {
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(id = R.string.app_name).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    modifier = Modifier
                        .layoutId(LayoutType.CONTENT)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TopLevelDestination.entries.forEach { appDestination ->
                        val selected = selectedDestination == appDestination.route
                        NavigationDrawerItem(selected = selected,
                            label = {
                                Text(
                                    text = stringResource(id = appDestination.iconTextId),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) appDestination.selectedIcon else appDestination.unselectedIcon,
                                    contentDescription = stringResource(
                                        id = appDestination.iconTextId
                                    )
                                )
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent
                            ),
                            onClick = { navigateToTopLevelDestination(appDestination) })
                    }
                }
            }, measurePolicy = navigationMeasurePolicy(navigationContentPosition)
        )
    }
}

@Composable
fun ModalNavigationDrawerContent(
    selectedDestination: AppRoutRoot,
    navigationContentPosition: NavigationContentPosition,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {}
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        // TODO remove custom nav drawer content positioning when NavDrawer component supports it. ticket : b/232495216
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(16.dp), content = {
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = onDrawerClicked) {
                            Icon(
                                imageVector = Icons.Default.MenuOpen,
                                contentDescription = stringResource(id = R.string.navigation_drawer)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .layoutId(LayoutType.CONTENT)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TopLevelDestination.entries.forEach { appDestination ->
                        val selected = selectedDestination == appDestination.route
                        NavigationDrawerItem(selected = selected,
                            label = {
                                Text(
                                    text = stringResource(id = appDestination.iconTextId),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) {
                                        appDestination.selectedIcon
                                    } else {
                                        appDestination.unselectedIcon
                                    },
                                    contentDescription = stringResource(
                                        id = appDestination.iconTextId
                                    )
                                )
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent
                            ),
                            onClick = { navigateToTopLevelDestination(appDestination) })
                    }
                }
            }, measurePolicy = navigationMeasurePolicy(navigationContentPosition)
        )
    }
}

fun navigationMeasurePolicy(
    navigationContentPosition: NavigationContentPosition,
): MeasurePolicy {
    return MeasurePolicy { measurables, constraints ->
        lateinit var headerMeasurable: Measurable
        lateinit var contentMeasurable: Measurable
        measurables.forEach {
            when (it.layoutId) {
                LayoutType.HEADER -> headerMeasurable = it
                LayoutType.CONTENT -> contentMeasurable = it
                else -> error("Unknown layoutId encountered!")
            }
        }

        val headerPlaceable = headerMeasurable.measure(constraints)
        val contentPlaceable = contentMeasurable.measure(
            constraints.offset(vertical = -headerPlaceable.height)
        )
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place the header, this goes at the top
            headerPlaceable.placeRelative(0, 0)

            // Determine how much space is not taken up by the content
            val nonContentVerticalSpace = constraints.maxHeight - contentPlaceable.height

            val contentPlaceableY = when (navigationContentPosition) {
                // Figure out the place we want to place the content, with respect to the
                // parent (ignoring the header for now)
                NavigationContentPosition.TOP -> 0
                NavigationContentPosition.CENTER -> nonContentVerticalSpace / 2
            }
                // And finally, make sure we don't overlap with the header.
                .coerceAtLeast(headerPlaceable.height)

            contentPlaceable.placeRelative(0, contentPlaceableY)
        }
    }
}

enum class LayoutType {
    HEADER, CONTENT
}
