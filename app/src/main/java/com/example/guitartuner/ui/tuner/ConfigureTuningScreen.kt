package com.example.guitartuner.ui.tuner

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.guitartuner.R
import com.example.guitartuner.ui.model.TuneButtonsUIState
import com.example.guitartuner.ui.model.TuningUIState
import com.example.guitartuner.ui.tuner.components.StringControls
import com.example.guitartuner.ui.tuner.components.TuningSelector
import com.example.guitartuner.ui.tuner.components.previewButtonsUIState
import com.example.guitartuner.ui.tuner.components.previewTuningState
import com.rohankhayech.android.util.ui.preview.CompactOrientationThemePreview
import com.rohankhayech.android.util.ui.preview.PreviewWrapper

/** UI screen used to tune individual strings and the tuning itself up and down.
 * @param tunings Map of tunings to their UI state.
 * @param selectedTuningId ID of the currently selected tuning.
 * @param buttonsUIState UI state of the tune up/down buttons.
 * @param onSelectTuning Called when a tuning is selected.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onOpenTuningSelector Called when the tuning selector is opened.
 * @param onDismiss Called when the screen is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureTuningScreen(
    tunings: State<List<TuningUIState>>,
    currentTuningSet: TuningUIState,
    buttonsUIState: TuneButtonsUIState,
    onSelectTuning: (Int) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()

    val appBarElevation by animateDpAsState(
        remember {
            derivedStateOf {
                if (scrollState.value == 0) {
                    0.dp
                } else AppBarDefaults.TopAppBarElevation
            }
        }.value,
        label = "App Bar Elevation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.configure_tuning))
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, stringResource(R.string.dismiss))
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = appBarElevation
            )
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth()) {
                Divider(thickness = Dp.Hairline)
                BottomAppBar(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.onBackground,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        TuningSelector(
                            currentTuningSet = currentTuningSet,
                            tunings = tunings.value,
                            openDirect = true,
                            onSelect = onSelectTuning,
                            onTuneDown = onTuneDownTuning,
                            onTuneUp = onTuneUpTuning,
                            onOpenTuningSelector = onOpenTuningSelector,
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(8.dp))
            StringControls(
                inline = true,
                buttonsUIState = buttonsUIState,
                selectedString = null,
                tuned = null,
                onSelect = {},
                onTuneDown = onTuneDownString,
                onTuneUp = onTuneUpString
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@CompactOrientationThemePreview
@Composable
private fun Preview() {
    PreviewWrapper {
        ConfigureTuningScreen(
            tunings = remember { mutableStateOf(previewTuningState) },
            buttonsUIState = previewButtonsUIState,
            currentTuningSet = previewTuningState[1],
            onSelectTuning = {},
            onTuneUpString = {},
            onTuneDownString = {},
            onTuneUpTuning = {},
            onTuneDownTuning = {},
            onOpenTuningSelector = {}
        ) {}
    }
}