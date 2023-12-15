package com.example.guitartuner.ui.tuner.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guitartuner.R
import com.example.guitartuner.domain.entity.tuner.Notation
import com.example.guitartuner.domain.entity.tuner.Note
import com.example.guitartuner.domain.entity.tuner.Pitch
import com.example.guitartuner.ui.model.TuneButtonsUIState
import com.example.guitartuner.ui.theme.PreviewWrapper
import com.rohankhayech.android.util.ui.preview.ThemePreview

/**
 * Component displaying each string in the current [tuning] and allowing selection of a string for tuning.
 * @param inline Whether to display the string controls inline or side-by-side.
 * @param tuning Current guitar tuning used for comparison.
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 * @param onTuneDown Called when a string is tuned down.
 * @param onTuneUp Called when a string is tuned up.
 */
@Composable
fun StringControls(
    inline: Boolean,
    buttonsUIState: TuneButtonsUIState,
    selectedString: Int?,
    tuned: BooleanArray?,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        if (inline) {
            InlineStringControls(
                buttonsUIState = buttonsUIState,
                selectedString = selectedString,
                tuned = tuned,
                onSelect = onSelect,
                onTuneDown = onTuneDown,
                onTuneUp = onTuneUp
            )
        } else {
            SideBySideStringControls(
                buttonsUIState = buttonsUIState,
                selectedString = selectedString,
                tuned = tuned,
                onSelect = onSelect,
                onTuneDown = onTuneDown,
                onTuneUp = onTuneUp
            )
        }
    }
}

/**
 * Component displaying each string in the current [tuning] side-by-side and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 * @param onTuneDown Called when a string is tuned down.
 * @param onTuneUp Called when a string is tuned up.
 */
@Composable
private fun SideBySideStringControls(
    buttonsUIState: TuneButtonsUIState,
    selectedString: Int?,
    tuned: BooleanArray?,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.spacedBy(8.dp)
    ) {

        val (buttonsUIState1, buttonsUIState2) = remember(buttonsUIState.tuningName) {
            val (tune1, tune2) = with(buttonsUIState.tuningName) { chunked(length / 2) }
            val (pitch1, pitch2) = with(buttonsUIState.pitchMap) {
                toList().chunked(size / 2).map { it.toMap() }
            }

            buttonsUIState.copy(
                tuningName = tune1,
                pitchMap = pitch1,
            ) to buttonsUIState.copy(
                tuningName = tune2,
                pitchMap = pitch2,
            )
        }

        InlineStringControls(
            buttonsUIState = buttonsUIState1,
            selectedString = selectedString,
            tuned = tuned,
            onSelect = onSelect,
            onTuneDown = onTuneDown,
            onTuneUp = onTuneUp
        )

        InlineStringControls(
            buttonsUIState = buttonsUIState2,
            selectedString = selectedString,
            tuned = tuned,
            onSelect = onSelect,
            onTuneDown = onTuneDown,
            onTuneUp = onTuneUp
        )
    }
}

/**
 * Component displaying the specified [strings] inline and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param strings Strings to display in this selector and their indexes within the tuning. Defaults to [tuning].
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 * @param onTuneDown Called when a string is tuned down.
 * @param onTuneUp Called when a string is tuned up.
 */
@Composable
private fun InlineStringControls(
    buttonsUIState: TuneButtonsUIState,
    selectedString: Int?,
    tuned: BooleanArray?,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        buttonsUIState.pitchMap.forEach { (index, _) ->
            StringControl(
                pitchKey = index,
                buttonsUIState = buttonsUIState,
                selected = selectedString == index,
                tuned = tuned?.get(index) ?: false,
                onSelect = onSelect,
                onTuneDown = onTuneDown,
                onTuneUp = onTuneUp,
            )
        }
    }
}

/**
 * Component displaying the specified [strings] inline horizontally and allowing selection of a string for tuning.
 * @param tuning Current guitar tuning used for comparison.
 * @param strings Strings to display in this selector and their indexes within the tuning. Defaults to [tuning].
 * @param selectedString Index of the selected string in the tuning.
 * @param tuned Whether each string has been tuned.
 * @param onSelect Called when a string is selected.
 */
@Composable
fun CompactStringSelector(
    modifier: Modifier = Modifier,
    buttonsUIState: TuneButtonsUIState,
    selectedString: Int,
    tuned: BooleanArray,
    onSelect: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()

    val selectedStringButtonPosition = with(LocalDensity.current) {
        remember(buttonsUIState.tuningName, selectedString) {
            (72.dp * (buttonsUIState.pitchMap.size - 1 - selectedString)).toPx()
        }
    }
    LaunchedEffect(key1 = selectedString) {
        scrollState.animateScrollTo(selectedStringButtonPosition.toInt())
    }

    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        buttonsUIState.pitchMap.forEach { (pitchKey, _) ->
            StringSelectionButton(
                pitchKey = pitchKey,
                buttonsUIState = buttonsUIState,
                selected = selectedString == pitchKey,
                tuned = tuned[pitchKey],
                onSelect = onSelect
            )
        }
        Spacer(Modifier.width(8.dp))
    }
}

/**
 * Row of buttons allowing selection and retuning of the specified string.
 *
 * @param pitchKey Index of the string within the tuning.
 * @param string The guitar string.
 * @param selected Whether the string is currently selected for tuning.
 * @param onSelect Called when the string is selected.
 * @param onTuneDown Called when the string is tuned down.
 * @param onTuneUp Called when the string is tuned up.
 */
@Composable
private fun StringControl(
    pitchKey: Int,
    buttonsUIState: TuneButtonsUIState,
    selected: Boolean,
    tuned: Boolean,
    onSelect: (Int) -> Unit,
    onTuneDown: (Int) -> Unit,
    onTuneUp: (Int) -> Unit,
) {
    val pitch = buttonsUIState.pitchMap[pitchKey] ?: return

    Row(verticalAlignment = Alignment.CenterVertically) {
        // Tune Down Button
        IconButton(
            onClick = remember(onTuneDown, pitchKey) { { onTuneDown(pitchKey) } },
            enabled = remember(pitch) { derivedStateOf { pitch.isValidPitch() } }.value
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = stringResource(R.string.tune_down)
            )
        }

        StringSelectionButton(pitchKey, buttonsUIState, tuned, selected, onSelect)

        // Tune Up Button
        IconButton(
            onClick = remember(onTuneUp, pitchKey) { { onTuneUp(pitchKey) } },
            enabled = remember(pitch) { derivedStateOf { pitch.isValidPitch() } }.value
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = stringResource(id = R.string.tune_up)
            )
        }
    }
}

/**
 * Buttons displaying and allowing user selection of the specified string.
 *
 * @param pitchKey Index of the string within the tuning.
 * @param string The guitar string.
 * @param tuned Whether the string is tuned.
 * @param selected Whether the string is currently selected for tuning.
 * @param onSelect Called when the string is selected.
 */
@Composable
private fun StringSelectionButton(
    pitchKey: Int,
    buttonsUIState: TuneButtonsUIState,
    tuned: Boolean,
    selected: Boolean,
    onSelect: (Int) -> Unit,
) {
    // Animate content color by selected and tuned state.
    val contentColor by animateColorAsState(
        with(MaterialTheme.colorScheme) {
            if (selected) {
                if (tuned) primary else tertiary
            } else if (tuned) {
                primary
            } else LocalContentColor.current
//            if (tuned) primary
//            else if (selected) tertiary
//            else LocalContentColor.current
        },
        label = "String Button Content Color"
    )

    // Animate background color by selected state.
    val containerColor by animateColorAsState(
        with(MaterialTheme.colorScheme) {
            if (selected) {
                if (tuned) primaryContainer else tertiaryContainer
            } else if (tuned) {
                onPrimary
            } else surface
        },
        label = "String Button Background Color"
    )

    val borderColor = containerColor.copy(alpha = 0.85f)
        .compositeOver(MaterialTheme.colorScheme.onSurfaceVariant)

    // Selection Button
    OutlinedButton(
        modifier = Modifier.defaultMinSize(72.dp, 48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor,
            containerColor = containerColor,
        ),
        border = BorderStroke(1.dp, borderColor),
//        shape = RoundedCornerShape(100),
        onClick = remember(onSelect, pitchKey) { { onSelect(pitchKey) } }
    ) {
        Text(buttonsUIState.getNoteFullName(pitchKey) ?: "", modifier = Modifier.padding(4.dp))
    }
}

// Previews

internal val previewButtonsUIState by lazy {
    TuneButtonsUIState(
        tuningId = 1,
        tuningName = "EBGDAE",
        pitchList = listOf(
            Pitch(0.0, Note.E, 2),
            Pitch(0.0, Note.B, 3),
            Pitch(0.0, Note.G, 3),
            Pitch(0.0, Note.D, 3),
            Pitch(0.0, Note.A, 2),
            Pitch(0.0, Note.E, 4),
        ),
        notation = Notation.English
    )
}

@ThemePreview
@Composable
fun InlinePreview() {
    PreviewWrapper {
        StringControls(
            inline = true,
            buttonsUIState = previewButtonsUIState,
            selectedString = 1,
            tuned = BooleanArray(6) { it == 4 },
            onSelect = {},
            onTuneDown = {},
            onTuneUp = {}
        )
    }
}

@ThemePreview
@Composable
private fun SideBySidePreview() {
    PreviewWrapper {
        StringControls(
            inline = false,
            buttonsUIState = previewButtonsUIState,
            selectedString = 1,
            tuned = BooleanArray(6) { it == 4 },
            onSelect = {},
            onTuneDown = {},
            onTuneUp = {}
        )
    }
}

@ThemePreview
@Composable
private fun CompactPreview() {
    PreviewWrapper {
        CompactStringSelector(
            buttonsUIState = previewButtonsUIState,
            selectedString = 5,
            tuned = BooleanArray(6) { it == 4 },
            onSelect = {},
        )
    }
}

@ThemePreview
@Composable
private fun StringControlPreview() {
    PreviewWrapper {
        StringControl(
            pitchKey = 0,
            buttonsUIState = previewButtonsUIState,
            selected = false,
            tuned = false,
            onSelect = {},
            onTuneDown = {},
            onTuneUp = {})
    }
}

@ThemePreview
@Composable
private fun ButtonStatesPreview() {
    PreviewWrapper {
        Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StringSelectionButton(
                tuned = false,
                selected = false,
                onSelect = {},
                pitchKey = 0,
                buttonsUIState = previewButtonsUIState
            )
            StringSelectionButton(
                tuned = false,
                selected = true,
                onSelect = {},
                pitchKey = 0,
                buttonsUIState = previewButtonsUIState
            )
            StringSelectionButton(
                tuned = true,
                selected = false,
                onSelect = {},
                pitchKey = 0,
                buttonsUIState = previewButtonsUIState
            )
            StringSelectionButton(
                tuned = true,
                selected = true,
                onSelect = {},
                pitchKey = 0,
                buttonsUIState = previewButtonsUIState
            )
        }
    }
}

@Preview(fontScale = 3f)
@Composable
private fun LargeFontPreview() {
    PreviewWrapper {
        StringControl(
            pitchKey = 0,
            buttonsUIState = previewButtonsUIState,
            selected = false,
            tuned = false,
            onSelect = {},
            onTuneDown = {},
            onTuneUp = {})
    }
}
