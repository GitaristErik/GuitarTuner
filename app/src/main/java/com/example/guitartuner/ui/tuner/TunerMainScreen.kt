@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.guitartuner.ui.tuner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.guitartuner.R
import com.example.guitartuner.domain.entity.settings.StringLayout
import com.example.guitartuner.domain.entity.settings.TunerPreferences
import com.example.guitartuner.ui.model.TuneButtonsUIState
import com.example.guitartuner.ui.model.TuningUIState
import com.example.guitartuner.ui.theme.PreviewWindowWrapper
import com.example.guitartuner.ui.theme.PreviewWrapper
import com.example.guitartuner.ui.tuner.components.CompactStringSelector
import com.example.guitartuner.ui.tuner.components.StringControls
import com.example.guitartuner.ui.tuner.components.TuningDisplay
import com.example.guitartuner.ui.tuner.components.TuningItem
import com.example.guitartuner.ui.tuner.components.TuningSelector
import com.example.guitartuner.ui.tuner.components.previewButtonsUIState
import com.example.guitartuner.ui.tuner.components.previewTuningState
import com.rohankhayech.android.util.ui.preview.CompactThemePreview
import com.rohankhayech.android.util.ui.preview.LandscapePreview
import com.rohankhayech.android.util.ui.preview.LargeFontPreview
import com.rohankhayech.android.util.ui.preview.ThemePreview


/** UI screen used to tune individual strings and the tuning
 *  itself up and down, as well as select from favourite tunings.
 *
 * @param compact Whether to use compact layout.
 * @param expanded Whether the current window is an expanded width.
 * @param windowSizeClass Size class of the activity window.
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param tunings Map of all tunings, keyed by their ID.
 * @param selectedTuningId ID of the currently selected tuning.
 * @param buttonsUIState UI state of the tuning buttons.
 * @param selectedString Index of the currently selected string within the tuning.
 * @param tuned Whether each string has been tuned.
 * @param autoDetect Whether the tuner will automatically detect the currently playing string.
 * @param prefs User preferences for the tuner.
 * @param onSelectString Called when a string is selected.
 * @param onSelectTuning Called when a tuning is selected.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onAutoChanged Called when the auto detect switch is toggled.
 * @param onTuned Called when the detected note is held in tune.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 * @param onSettingsPressed Called when the settings button is pressed.
 * @param onConfigurePressed Called when the configure button is pressed.
 */
@Composable
fun TunerMainScreen(
    compact: Boolean = false,
    expanded: Boolean = false,
    windowSizeClass: WindowSizeClass,
    noteOffset: State<Double?>,
    tunings: State<Map<Int, TuningUIState>>,
    selectedTuningId: Int,
    buttonsUIState: TuneButtonsUIState,
    selectedString: Int,
    tuned: BooleanArray,
    autoDetect: Boolean,
    prefs: TunerPreferences,
    onSelectString: (Int) -> Unit,
    onSelectTuning: (Int) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    onTuned: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    onSettingsPressed: () -> Unit,
    onConfigurePressed: () -> Unit
) {
    Scaffold(
        topBar = {
            if (!compact) {
                AppBar(onSettingsPressed)
            } else {
                CompactAppBar(
                    tuning = tunings.value.let {
                        it[buttonsUIState.tuningId] ?: it.values.first()
                    },
                    onSettingsPressed = onSettingsPressed,
                    onConfigurePressed = onConfigurePressed
                )
            }
        }
    ) { padding ->
        TunerBodyScaffold(
            padding,
            compact,
            expanded,
            windowSizeClass,
            tunings,
            selectedTuningId,
            buttonsUIState,
            noteOffset,
            selectedString,
            tuned,
            autoDetect,
            prefs,
            onSelectString,
            onSelectTuning,
            onTuneUpString,
            onTuneDownString,
            onTuneUpTuning,
            onTuneDownTuning,
            onAutoChanged,
            onTuned,
            onOpenTuningSelector,

            // Portrait layout
            portrait = { padd, tuningDisplay, stringControls, autoDetectSwitch, tuningSelector ->
                Column(
                    modifier = Modifier
//                        .padding(padd)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    tuningDisplay()
                    stringControls(prefs.stringLayout == StringLayout.INLINE)
                    autoDetectSwitch(Modifier)
                    tuningSelector(Modifier)
                }
            },

            // Landscape layout
            landscape = { padd, tuningDisplay, stringControls, autoDetectSwitch, tuningSelector ->
                ConstraintLayout(
                    modifier = Modifier
                        .padding(padd)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    val (display, tuningSelectorBox, stringsSelector, autoSwitch) = createRefs()

                    Box(Modifier.constrainAs(display) {
                        top.linkTo(parent.top)
                        bottom.linkTo(autoSwitch.top)
                        start.linkTo(parent.start)
                        end.linkTo(stringsSelector.start)
                    }) {
                        tuningDisplay()
                    }

                    Box(Modifier.constrainAs(tuningSelectorBox) {
                        top.linkTo(stringsSelector.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                        tuningSelector(Modifier)
                    }

                    Box(Modifier.constrainAs(stringsSelector) {
                        top.linkTo(parent.top)
                        bottom.linkTo(tuningSelectorBox.top)
                        start.linkTo(display.end)
                        end.linkTo(parent.end)
                    }) {
                        stringControls(
                            windowSizeClass.heightSizeClass > WindowHeightSizeClass.Compact
                                    && prefs.stringLayout == StringLayout.INLINE,
                        )
                    }

                    Box(Modifier.constrainAs(autoSwitch) {
                        top.linkTo(display.bottom)
                        bottom.linkTo(tuningSelectorBox.top)
                        start.linkTo(tuningSelectorBox.start)
                        end.linkTo(stringsSelector.start)
                    }) {
                        autoDetectSwitch(Modifier)
                    }
                }
            },

            // Compact layout
            compactLayout = { padd, tuningDisplay, _, autoDetectSwitch, _ ->
                Column(
                    modifier = Modifier
//                        .padding(padd)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center,
                    ) {
                        tuningDisplay()
                    }
                    Row(
                        Modifier
                            .height(IntrinsicSize.Min)
                            .padding(bottom = 8.dp)
                    ) {
                        CompactStringSelector(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            buttonsUIState = buttonsUIState,
                            selectedString = selectedString,
                            tuned = tuned,
                            onSelect = onSelectString,
                        )
                        Divider(
                            Modifier
                                .width((1f / LocalDensity.current.density).dp)
                                .fillMaxHeight()
                        )
                        Box(Modifier.padding(horizontal = 16.dp)) {
                            autoDetectSwitch(Modifier.fillMaxHeight())
                        }
                    }
                }
            }
        )
    }
}

/**
 * Type of layout for the tuner screen body.
 * Should place all appropriate components provided,
 * and must use the provided padding for the root composable.
 */
private typealias TunerBodyLayout = @Composable (
    padding: PaddingValues,
    tuningDisplay: @Composable () -> Unit,
    stringControls: @Composable (inline: Boolean) -> Unit,
    autoDetectSwitch: @Composable (modifier: Modifier) -> Unit,
    tuningSelector: @Composable (modifier: Modifier) -> Unit
) -> Unit

/**
 * Scaffold containing the main UI components of the tuner screen body.
 * The scaffold places these components in the appropriate layout.
 *
 * [Portrait][portrait], [landscape] and [compact][compactLayout] layouts
 * must be defined to determine the placement of the UI components.
 *
 * @param compact Whether to use compact layout.
 * @param expanded Whether the current window is an expanded width.
 * @param windowSizeClass Size class of the activity window.
 * @param tunings Map of all tunings, keyed by their ID.
 * @param selectedTuningId ID of the currently selected tuning.
 * @param buttonsUIState UI state of the tuning buttons.
 * @param noteOffset The offset between the currently playing note and the selected string.
 * @param selectedString Index of the currently selected string within the tuning.
 * @param tuned Whether each string has been tuned.
 * @param autoDetect Whether the tuner will automatically detect the currently playing string.
 * @param prefs User preferences for the tuner.
 * @param onSelectString Called when a string is selected.
 * @param onSelectTuning Called when a tuning is selected.
 * @param onTuneUpString Called when a string is tuned up.
 * @param onTuneDownString Called when a string is tuned down.
 * @param onTuneUpTuning Called when the tuning is tuned up.
 * @param onTuneDownTuning Called when the tuning is tuned down.
 * @param onAutoChanged Called when the auto detect switch is toggled.
 * @param onTuned Called when the detected note is held in tune.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TunerBodyScaffold(
    padding: PaddingValues,
    compact: Boolean = false,
    expanded: Boolean,
    windowSizeClass: WindowSizeClass,
    tunings: State<Map<Int, TuningUIState>>,
    selectedTuningId: Int,
    buttonsUIState: TuneButtonsUIState,
    noteOffset: State<Double?>,
    selectedString: Int,
    tuned: BooleanArray,
    autoDetect: Boolean,
    prefs: TunerPreferences,
    onSelectString: (Int) -> Unit,
    onSelectTuning: (Int) -> Unit,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onAutoChanged: (Boolean) -> Unit,
    onTuned: () -> Unit,
    onOpenTuningSelector: () -> Unit,
    portrait: TunerBodyLayout,
    landscape: TunerBodyLayout,
    compactLayout: TunerBodyLayout
) {
    val layout = if (!compact) {
        if ((windowSizeClass.heightSizeClass >= WindowHeightSizeClass.Medium && windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact)
            || (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Expanded && windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium)
        ) {
            portrait
        } else {
            landscape
        }
    } else {
        compactLayout
    }

    layout(
        padding, {
            TuningDisplay(
                noteOffset = noteOffset,
                displayType = prefs.displayType,
                onTuned = onTuned
            )
        }, { inline ->
            StringControls(
                inline = inline,
                buttonsUIState = buttonsUIState,
                selectedString = selectedString,
                tuned = tuned,
                onSelect = onSelectString,
                onTuneDown = onTuneDownString,
                onTuneUp = onTuneUpString
            )
        }, { modifier ->
            AutoDetectSwitch(
                modifier = modifier,
                autoDetect = autoDetect,
                onAutoChanged = onAutoChanged
            )
        }, { modifier ->
            TuningSelector(
                modifier = modifier,
                selectedTuningId = selectedTuningId,
                tunings = tunings,
                openDirect = false,
                onSelect = onSelectTuning,
                onTuneDown = onTuneDownTuning,
                onTuneUp = onTuneUpTuning,
                onOpenTuningSelector = onOpenTuningSelector,
                enabled = !expanded
            )
        }
    )
}

/**
 * UI screen shown to the user when the audio permission is not granted.
 *
 * @param canRequest Whether the permission can be requested.
 * @param onSettingsPressed Called when the settings navigation button is pressed.
 * @param onRequestPermission Called when the request permission button is pressed.
 * @param onOpenPermissionSettings Called when the open permission settings button is pressed.
 */
@Composable
fun TunerPermissionScreen(
    canRequest: Boolean,
    onSettingsPressed: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenPermissionSettings: () -> Unit,
) {
    Scaffold(
        topBar = { AppBar(onSettingsPressed) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(
                16.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val title: String
            val rationale: String
            val buttonLabel: String
            val buttonAction: () -> Unit
            if (canRequest) {
                title = stringResource(R.string.permission_needed)
                rationale = stringResource(R.string.tuner_audio_permission_rationale)
                buttonLabel = stringResource(R.string.request_permission).uppercase()
                buttonAction = onRequestPermission
            } else {
                title = stringResource(R.string.permission_denied)
                rationale = stringResource(R.string.tuner_audio_permission_rationale_denied)
                buttonLabel = stringResource(R.string.open_permission_settings).uppercase()
                buttonAction = onOpenPermissionSettings
            }

            Text( // Title
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Text( // Rationale
                text = rationale,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 256.dp)
            )
            // Action Button
            Button(onClick = buttonAction) {
                Text(buttonLabel, textAlign = TextAlign.Center)
            }
        }
    }
}

/**
 * App bar for the tuning screen.
 * @param onSettingsPressed Called when the settings button is pressed.
 */
@Composable
private fun AppBar(
    onSettingsPressed: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            IconButton(onClick = onSettingsPressed) {
                Icon(Icons.Default.Settings, stringResource(R.string.tuner_settings))
            }
        }
    )
}

/**
 * App bar for the tuning screen.
 * @param onSettingsPressed Called when the settings button is pressed.
 * @param onConfigurePressed Called when the configure button is pressed.
 * @param tuning Currently selected tuning.
 */
@Composable
private fun CompactAppBar(
    onSettingsPressed: () -> Unit,
    onConfigurePressed: () -> Unit,
    tuning: TuningUIState,
) {
    TopAppBar(
        title = {
            TuningItem(tuning = tuning, fontWeight = FontWeight.Bold)
        },
        actions = {
            // Configure tuning button.
            IconButton(onClick = onConfigurePressed) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = stringResource(R.string.configure_tuning)
                )
            }

            // Settings button
            IconButton(onClick = onSettingsPressed) {
                Icon(Icons.Default.Settings, stringResource(R.string.tuner_settings))
            }
        }
    )
}

/**
 * Switch control allowing auto detection of string to be enabled/disabled.
 *
 * @param autoDetect Whether auto detection is enabled.
 * @param onAutoChanged Called when the switch is toggled.
 */
@Composable
private fun AutoDetectSwitch(
    modifier: Modifier = Modifier,
    autoDetect: Boolean,
    onAutoChanged: (Boolean) -> Unit
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.auto_detect_label).uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.paddingFromBaseline(bottom = 6.dp)
        )
        Switch(checked = autoDetect, onCheckedChange = onAutoChanged)
    }
}

// PREVIEWS

@Composable
private fun PreviewTunerWrapper(
    compact: Boolean = false,
    prefs: TunerPreferences = TunerPreferences(),
) {
    PreviewWindowWrapper { windowSizeClass ->
        TunerMainScreen(
            compact,
            expanded = false,
            windowSizeClass,
            noteOffset = remember { mutableDoubleStateOf(1.3) },
            tunings = remember { mutableStateOf(previewTuningState) },
            selectedTuningId = 1,
            buttonsUIState = previewButtonsUIState,
            selectedString = 1,
            tuned = BooleanArray(6) { it == 4 },
            autoDetect = true,
            prefs = prefs,
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}

@ThemePreview
@Composable
private fun TunerPreview() {
    PreviewTunerWrapper()
}

@CompactThemePreview
@Composable
private fun CompactPreview() {
    PreviewTunerWrapper(
        compact = true
    )
}

@LandscapePreview
@Composable
private fun LandscapePreview() {
    PreviewTunerWrapper()
}

@LargeFontPreview
@Composable
private fun LargeFontPreview() {
    PreviewTunerWrapper()
}

@Preview
@Composable
private fun PermissionRequestPreview() {
    PreviewWrapper {
        TunerPermissionScreen(
            canRequest = true,
            onSettingsPressed = {},
            onRequestPermission = {},
            onOpenPermissionSettings = {}
        )
    }
}

@Preview
@Composable
private fun PermissionDeniedPreview() {
    PreviewWrapper {
        TunerPermissionScreen(
            canRequest = false,
            onSettingsPressed = {},
            onRequestPermission = {},
            onOpenPermissionSettings = {}
        )
    }
}
