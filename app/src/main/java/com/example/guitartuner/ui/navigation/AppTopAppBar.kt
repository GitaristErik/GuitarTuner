package com.example.guitartuner.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

object TopAppBarProvider {

    @JvmStatic
    @OptIn(ExperimentalMaterial3Api::class)
    var defaultTopAppBar: @Composable (String) -> Unit = { selectedDestination ->
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = { defaultTitle(selectedDestination) },
            navigationIcon = defaultNavigationIcon,
            actions = defaultActions
        )
    }

    @JvmStatic
    var defaultTitle: @Composable (String) -> Unit = { selectedDestination ->
        AnimatedContent(
            targetState = selectedDestination,
            label = "Top App Bar"
        ) { destination ->
            Text(
                text = stringResource(
                    id = with(TOP_LEVEL_DESTINATIONS) {
                        firstOrNull { it.route == destination } ?: first()
                    }.iconTextId
                )
            )
        }
    }

    @JvmStatic
    var defaultActions: @Composable RowScope.() -> Unit = {}

    @JvmStatic
    var defaultNavigationIcon: @Composable () -> Unit = {}

}