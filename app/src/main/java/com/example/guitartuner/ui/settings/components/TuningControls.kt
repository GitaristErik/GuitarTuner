package com.example.guitartuner.ui.settings.components

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.guitartuner.R
import com.example.guitartuner.ui.model.FilterBoxUIState
import com.example.guitartuner.ui.model.TuningSettingsUIState
import com.example.guitartuner.ui.settings.components.TuningControls.SectionHeader
import com.example.guitartuner.ui.settings.components.TuningControls.TuningSettingsItem
import com.example.guitartuner.ui.theme.PreviewWrapper
import com.rohankhayech.android.util.ui.preview.ThemePreview

object TuningControls {
    /**
     * This composable function creates a filter box with multiple filter chips.
     * @param values The list of filter box states to be displayed.
     * @param onSelect The function to be called when a filter chip is selected.
     */
    @Composable
    fun <T> FilterBox(
        values: List<FilterBoxUIState<T>>, onSelect: (T, Boolean) -> Unit
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .animateContentSize()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 0.dp)
        ) {
            items(values.size, key = { values[it].key }) { index ->
                TuningFilterChip(
                    filter = values[index].value,
                    filterText = values[index].text,
                    enabled = values[index].isEnabled,
                    key = values[index].key,
                    onSelect = onSelect
                )
            }
        }
    }

    /**
     * This composable function creates a filter chip for tuning filters.
     * @param filter The filter to display.
     * @param filterText The localised filter name.
     * @param enabled Whether the filter is enabled to be selected.
     * @param selected Whether the filter is currently selected.
     * @param onSelect Called when the filter is selected/unselected.
     */
    @Composable
    private fun <T> TuningFilterChip(
        filter: T,
        filterText: String,
        enabled: Boolean,
        onSelect: (T, Boolean) -> Unit,
        selected: Boolean? = null,
        key: String? = null
    ) {
        val (isSelected, selectChange) = if (selected == null) {
            var state by rememberSaveable(key) { mutableStateOf(false) }
            state to { select: Boolean -> state = select }
        } else selected to { _ -> }

        FilterChip(modifier = Modifier.animateContentSize(),
            enabled = enabled,
            selected = isSelected,
            onClick = {
                if (enabled) {
                    selectChange(!isSelected)
                    onSelect(filter, !isSelected)
                }
            },
            leadingIcon = {
                if (isSelected) Icon(
                    Icons.Default.Done,
                    filterText + stringResource(R.string.settings_tunings_filter_desc)
                )
            },
            label = { Text(filterText) })
    }

    /**
     * This composable function creates a UI component displaying a tuning category label with [title] text.
     * @param title The title of the section.
     */
    @Composable
    fun LazyItemScope.SectionHeader(title: String) {
        SectionLabel(
            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
            title = title
        )
    }

    /**
     * This composable function creates a list item displaying a tuning.
     * @param tuning The tuning to display.
     * @param onSelect Called when this tuning is selected.
     * @param onFavSelect Called when the favourite button is pressed.
     * @param onSave Called when the save button is pressed.
     */
    @Composable
    fun LazyItemScope.TuningSettingsItem(
        tuning: TuningSettingsUIState,
        onSelect: (Int) -> Unit = {},
        onFavSelect: (Int, Boolean) -> Unit = { _, _ -> },
        onSave: ((TuningSettingsUIState) -> Unit)? = null,
    ) = with(tuning) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .animateItem(fadeInSpec = null, fadeOutSpec = null)
                .clickable { onSelect(tuningId) }
        ) {
            Box(Modifier.fillMaxWidth()) {

                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 24.dp, top = 16.dp, bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.CenterStart),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            Text(
                                text = instrumentName,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                textAlign = TextAlign.Left,
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Text(
                                text = stringResource(R.string.settings_tunings_instrument_divider),
                                overflow = TextOverflow.Clip,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Left,
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Text(
                                text = instrumentDetails + pluralStringResource(
                                    R.plurals.settings_tunings_instrument_details_suffix,
                                    instrumentDetails.toIntOrNull() ?: 0
                                ),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Left,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }

                        Text(
                            text = tuningName,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Text(
                            text = notesList,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    // fav select button icon
                    if (onSave == null || !isCustom) {
                        IconToggleButton(modifier = Modifier.align(Alignment.CenterEnd),
                            checked = isFavorite,
//                            colors = IconButtonDefaults.iconToggleButtonColors(
//                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
//                                contentColor = MaterialTheme.colorScheme.secondary
//                            ),
                            onCheckedChange = { onFavSelect(tuningId, !isFavorite) }) {
                            Icon(
                                if (isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = stringResource(R.string.settings_tunings_favourite_desc),
                            )
                        }
                    } else {
                        FilledTonalIconButton(modifier = Modifier.align(Alignment.CenterEnd),
//                            colors = IconButtonDefaults.iconButtonColors(
//                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
//                                contentColor = MaterialTheme.colorScheme.secondary
//                            ),
                            onClick = { onSave(tuning) }) {
                            Icon(
                                Icons.Default.SaveAlt,
                                contentDescription = stringResource(R.string.settings_tunings_save_desc)
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                )
            }
        }
    }

    /**
     * This composable function creates a UI component displaying a tuning set.
     * This realization has swipe to dismiss functionality, allowing the user to edit or remove the tuning.
     *
     * @param tuning The tuning to display.
     * @param onSelect Called when this tuning is selected.
     * @param onFavSelect Called when the user switch to favourite the tuning.
     * @param onRemove Called when the user swipes to remove the tuning.
     * @param modifier The modifier to apply to this layout node.
     */
    @Composable
    fun LazyItemScope.TuningSettingsItemSwippable(
        tuning: TuningSettingsUIState,
        onSelect: (Int) -> Unit = {},
        onFavSelect: (Int, Boolean) -> Unit = { _, _ -> },
        onRemove: (Int) -> Unit = {},
        modifier: Modifier = Modifier,
    ) = with(tuning) {
//        val currentItem by rememberUpdatedState(emailMessage)
        val context = LocalContext.current
        val message = stringResource(R.string.settings_tunings_remove_toast)

        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                when (it) {
                    Settled -> return@rememberSwipeToDismissBoxState false
                    StartToEnd -> return@rememberSwipeToDismissBoxState false
                    EndToStart -> {
                        onRemove(tuningId)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
                return@rememberSwipeToDismissBoxState true
            },
            positionalThreshold = { it * .25f }
        )
        SwipeToDismissBox(
            state = dismissState,
            modifier = modifier,
            backgroundContent = { DismissBackground(dismissState) },
            content = {
                TuningSettingsItem(
                    tuning = tuning,
                    onSelect = onSelect,
                    onFavSelect = onFavSelect,
                )
            })
    }


    @Composable
    fun DismissBackground(dismissState: SwipeToDismissBoxState) {
        val color = when (dismissState.dismissDirection) {
            EndToStart -> MaterialTheme.colorScheme.inverseSurface
            StartToEnd -> MaterialTheme.colorScheme.errorContainer
            Settled -> Color.Transparent
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
                .padding(12.dp, 8.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Icon(
                Icons.Default.Delete,
                tint = MaterialTheme.colorScheme.onError,
                contentDescription = stringResource(R.string.settings_tunings_remove_desc),
            )
        }
    }


    /**
     * This composable function creates a list item displaying a custom tuning, with options to favourite or remove it.
     * @param tuning The tuning to display.
     * @param onSelect Called when this tuning is selected.
     * @param onFavSelect Called when the favourite button is pressed.
     * @param onRemove Called when this tuning is swiped to be removed.
     */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun LazyItemScope.TuningSettingsItemSwippable2(
        tuning: TuningSettingsUIState,
        onSelect: (Int) -> Unit,
        onFavSelect: (Int, Boolean) -> Unit,
        onRemove: (Int) -> Unit,
    ) {
        val dismissState = rememberDismissState(confirmStateChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToStart) {
                onRemove(tuning.tuningId)
                true
            } else false
        })

        SwipeToDismiss(modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
            state = dismissState,
            directions = setOf(DismissDirection.EndToStart),
            background = {
                val color by animateColorAsState(
                    when (dismissState.currentValue) {
                        DismissValue.DismissedToStart -> MaterialTheme.colorScheme.errorContainer
//                            .copy(alpha = 0.36f)
//                            .compositeOver(MaterialTheme.colorScheme.surface)

                        else -> MaterialTheme.colorScheme.onErrorContainer
//                            .copy(alpha = 0.05f)
//                            .compositeOver(MaterialTheme.colorScheme.surface)
                    }, label = "Tuning Item Background Color"
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        contentDescription = stringResource(R.string.settings_tunings_delete_desc),
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            },
            dismissContent = {
                TuningSettingsItem(
                    tuning = tuning,
                    onSelect = onSelect,
                    onFavSelect = onFavSelect
                )
            })
    }

    /**
     * Dialog allowing the user to enter a name and save the specified tuning.
     * @param modifier The modifier to apply to this layout node.
     * @param tuningName The name of the tuning to save.
     * @param onSave Called when the user presses the save button.
     * @param onDismiss Called when the user dismisses the dialog.
     */
    @Composable
    fun SaveTuningDialog(
        modifier: Modifier,
        tuningName: String,
        onDismiss: () -> Unit,
        onSave: (String) -> Unit,
    ) {
        var name by rememberSaveable { mutableStateOf("") }

        AlertDialog(
            modifier = modifier,
            title = {
                Text(
                    stringResource(R.string.settings_tunings_alert_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                TextField(modifier = Modifier.fillMaxWidth(),
                    value = name,
                    placeholder = { Text(tuningName) },
                    singleLine = true,
                    onValueChange = { name = it })
            },
            confirmButton = {
                TextButton(onClick = {
                    onSave(name)
                }) {
                    Text(text = stringResource(R.string.settings_tunings_alert_save).uppercase())
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = stringResource(R.string.settings_tunings_alert_cancel).uppercase())
                }
            },
            onDismissRequest = onDismiss,
        )
    }
}

// -------------
// Previews
@Composable
@ThemePreview
private fun PreviewTuningsFav() {
    PreviewWrapper {
        LazyColumn {
            item {
                SectionHeader("Favourites")
            }
            item {
                TuningSettingsItem(tuning = makePreviewTuningState(0, false, false), {}, {_,_ ->}, {})
            }
            item {
                TuningSettingsItem(tuning = makePreviewTuningState(1, true, false), {}, {_,_ ->}, {})
            }
            item {
                TuningSettingsItem(tuning = makePreviewTuningState(2, false, false), {}, {_,_ ->}, {})
            }
            item {
                TuningSettingsItem(tuning = makePreviewTuningState(3, true, false), {}, {_,_ ->}, {})
            }
            item {
                TuningSettingsItem(tuning = makePreviewTuningState(4, false, false), {}, {_,_ ->}, {})
            }
            item {
                TuningSettingsItem(tuning = makePreviewTuningState(5, true, false), {}, {_,_ ->}, {})
            }
        }
    }
}

private fun makePreviewTuningState(variant: Int, isFavorite: Boolean, isCustom: Boolean) =
    when (variant) {
        1 -> TuningSettingsUIState(
            tuningId = 1,
            instrumentName = "Electric Guitar",
            instrumentDetails = "6",
            tuningName = "Drop D",
            notesList = "D2 A2 D3 G3 B3 E4",
            isFavorite = isFavorite,
            isCustom = isCustom
        ) // 12s acoustic guitar
        2 -> TuningSettingsUIState(
            tuningId = 2,
            instrumentName = "Acoustic Guitar",
            instrumentDetails = "12",
            tuningName = "Standard",
            notesList = "E2 E2 A2 A2 D3 D3 G3 G3 B3 B3 E4 E4",
            isFavorite = isFavorite,
            isCustom = isCustom
        ) // 5s bass guitar
        3 -> TuningSettingsUIState(
            tuningId = 3,
            instrumentName = "Bass Guitar",
            instrumentDetails = "5",
            tuningName = "Standard",
            notesList = "B0 E1 A1 D2 G2",
            isFavorite = isFavorite,
            isCustom = isCustom
        ) // 4s bass guitar
        4 -> TuningSettingsUIState(
            tuningId = 4,
            instrumentName = "Bass Guitar",
            instrumentDetails = "4",
            tuningName = "Standard",
            notesList = "E1 A1 D2 G2",
            isFavorite = isFavorite,
            isCustom = isCustom
        ) // 8s electric guitar
        5 -> TuningSettingsUIState(
            tuningId = 5,
            instrumentName = "Electric Guitar",
            instrumentDetails = "8",
            tuningName = "Standard",
            notesList = "F#1 B1 E2 A2 D3 G3 B3 E4",
            isFavorite = isFavorite,
            isCustom = isCustom
        )

        else -> TuningSettingsUIState(
            tuningId = 0,
            instrumentName = "Guitar",
            instrumentDetails = "6",
            tuningName = "Standard",
            notesList = "E2 A2 D3 G3 B3 E4",
            isFavorite = isFavorite,
            isCustom = isCustom
        )
    }

@Composable
@ThemePreview
private fun PreviewTuningsCustom() {
    PreviewWrapper {
        LazyColumn {
            item {
                SectionHeader("Custom Tunings")
            }
            item {
                TuningSettingsItem(tuning = makePreviewTuningState(0, false, true), {}, {_,_ ->}, {})
            }
            item {
                TuningSettingsItem(tuning = makePreviewTuningState(1, false, true), {}, {_,_ ->}, {})
            }
            item {
                TuningSettingsItem(tuning = makePreviewTuningState(2, true, true), {}, {_,_ ->}, {})
            }
            item {
                TuningSettingsItem(tuning = makePreviewTuningState(3, false, true), {}, {_,_ ->}, {})
            }

            item {
                TuningSettingsItem(tuning = makePreviewTuningState(4, true, true), {}, {_,_ ->}, {})
            }

            item {
                TuningSettingsItem(tuning = makePreviewTuningState(5, false, true), {}, {_,_ ->}, {})
            }
        }
    }
}

@ThemePreview
@Composable
private fun PreviewAlert() {
    PreviewWrapper {
        TuningControls.SaveTuningDialog(modifier = Modifier.fillMaxSize(),
            tuningName = "Drop C#",
            onDismiss = { },
            onSave = { _ -> })
    }
}