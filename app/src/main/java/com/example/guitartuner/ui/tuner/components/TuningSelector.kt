@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.guitartuner.ui.tuner.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.guitartuner.R
import com.example.guitartuner.ui.model.TuningUIState
import com.example.guitartuner.ui.theme.PreviewWrapper
import com.rohankhayech.android.util.ui.preview.ThemePreview

/**
 * Row UI component displaying and allowing selection and retuning of the current tuning.
 *
 * @param tuning The current guitar tuning.
 * @param favTunings Set of tunings marked as favourite by the user.
 * @param customTunings Set of custom tunings added by the user.
 * @param enabled Whether the selector is enabled. Defaults to true.
 * @param openDirect Whether to open the tuning selection screen directly instead of the favourites dropdown.
 * @param onSelect Called when a tuning is selected.
 * @param onTuneDown Called when the tuning is tuned down.
 * @param onTuneUp Called when the tuning is tuned up.
 * @param onOpenTuningSelector Called when the user opens the tuning selector screen.
 */
@ExperimentalMaterial3Api

@Composable
fun TuningSelector(
    modifier: Modifier = Modifier,
    currentTuningSet: TuningUIState,
    tunings: Map<Int, TuningUIState>,
    enabled: Boolean = true,
    openDirect: Boolean,
    onSelect: (Int) -> Unit,
    onTuneDown: () -> Unit,
    onTuneUp: () -> Unit,
    onOpenTuningSelector: () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val tintColor = MaterialTheme.colorScheme.tertiary
        // Tune Down Button
        IconButton(
            onClick = onTuneDown,
            /*  enabled = remember(selectedTuningId) {
                  derivedStateOf { tuning.min().rootNoteIndex > Tuner.LOWEST_NOTE }
              }.value*/
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                stringResource(R.string.tune_down),
                tint = tintColor,
            )
        }

        // Tuning Display and Selection
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(1f),
            expanded = expanded && enabled,
            onExpandedChange = {
                if (openDirect) onOpenTuningSelector()
                else expanded = !expanded
            }
        ) {

            // Current Tuning
            CurrentTuningField(
                modifier = Modifier.menuAnchor(),
                tuning = currentTuningSet,
                expanded = expanded,
                showExpanded = enabled
            )

            // Dropdown Menu
            ExposedDropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false }
            ) {
                tunings.forEach { (tuningId, tuningOption) ->
                    DropdownMenuItem(
                        text = {
                            TuningItem(
                                modifier = Modifier.padding(vertical = 8.dp),
                                tuning = tuningOption,
                                fontWeight = FontWeight.Normal,
                            )
                        }, onClick = {
                            onSelect(tuningId)
                            expanded = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text(stringResource(id = R.string.open_tuning_selector))
                    }, onClick = {
                        onOpenTuningSelector()
                        expanded = false
                    }
                )
            }
        }

        // Tune Up Button
        IconButton(
            onClick = onTuneUp,
            /*            enabled = remember(tuning) {
                            derivedStateOf { tuning.max().rootNoteIndex < Tuner.HIGHEST_NOTE }
                        }.value*/
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                stringResource(id = R.string.tune_up),
                tint = tintColor,
            )
        }
    }
}

/**
 * Outlined dropdown box field showing the current tuning.
 *
 * @param tuning The current guitar tuning.
 * @param expanded Whether the dropdown box is expanded.
 * @param showExpanded Whether to show the expanded state.
 */
@Composable
private fun CurrentTuningField(
    modifier: Modifier = Modifier,
    tuning: TuningUIState,
    expanded: Boolean,
    showExpanded: Boolean
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = OutlinedTextFieldDefaults.shape,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
//        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        border = BorderStroke(
            width = if (expanded && showExpanded) OutlinedTextFieldDefaults.FocusedBorderThickness
            else OutlinedTextFieldDefaults.UnfocusedBorderThickness,
            color = if (expanded && showExpanded) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.outlineVariant
        ),

        ) {
        Row(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TuningItem(
                modifier = Modifier.weight(1f),
                tuning = tuning,
                fontWeight = FontWeight.Bold
            )
            if (showExpanded) {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        }
    }
}

/**
 * UI component displaying the name and strings of the specified tuning.
 *
 * @param modifier The modifier to apply to this layout.
 * @param tuning The tuning to display.
 * @param fontWeight The font weight of the tuning name text.
 */
@Composable
fun TuningItem(
    modifier: Modifier = Modifier,
    tuning: TuningUIState,
    fontWeight: FontWeight,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            tuning.tuningName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            tuning.notesList,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

internal val previewTuningState: Map<Int, TuningUIState> by lazy {
    listOf(
        "Standard" to "E2, A2, D3, G3, B3, E4",
        "Half Step Down (D#)" to "D#2, G#2, C#3, F#3, A#3, D#4",
        "1 Step Down (D)" to "D2, G2, C3, F3, A3, D4",
        "Drop D" to "D2, A2, D3, G3, B3, E4",
        "Drop C" to "C2, G2, C3, F3, A3, D4",
        "Open G" to "D2, G2, D3, G3, B3, D4",
        "Open D" to "D2, A2, D3, F#3, A3, D4",
        "All 4th" to "E2, A2, D3, G3, C4, F4",
        "G Modal" to "D2, G2, D3, G3, C4, D4",
    ).mapIndexed { index, (name, notes) ->
        (index + 1) to TuningUIState(
            tuningName = name,
            notesList = notes.replace(" ", " ")
        )
    }.toMap()
}

// Previews
@ThemePreview
@Composable
private fun PreviewTuningSelector() {
    PreviewWrapper {
        TuningSelector(
            Modifier.padding(8.dp),
            currentTuningSet = previewTuningState[1]!!,
            tunings = previewTuningState,
            openDirect = false,
            onSelect = {},
            onTuneDown = {},
            onTuneUp = {},
            onOpenTuningSelector = {}
        )
    }
}

@ThemePreview
@Composable
private fun PreviewTuningField() {
    PreviewWrapper {
        Column {
            CurrentTuningField(
                tuning = previewTuningState[1]!!,
                expanded = false,
                showExpanded = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            CurrentTuningField(
                tuning = previewTuningState[1]!!,
                expanded = false,
                showExpanded = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            CurrentTuningField(
                tuning = previewTuningState[1]!!,
                expanded = true,
                showExpanded = false
            )


            Spacer(modifier = Modifier.height(32.dp))

            CurrentTuningField(
                tuning = previewTuningState[1]!!,
                expanded = true,
                showExpanded = true
            )

        }
    }
}

@ThemePreview
@Composable
private fun PreviewTuningItem() {
    PreviewWrapper {
        TuningItem(
            Modifier.padding(8.dp),
            tuning = previewTuningState[1]!!,
            fontWeight = FontWeight.Bold
        )
    }
}
