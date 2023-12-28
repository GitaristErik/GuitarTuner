package com.example.guitartuner.ui.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.guitartuner.R
import com.example.guitartuner.domain.repository.tuner.TuningSetsRepository.TuningFilterBuilder.TuningFilter
import com.example.guitartuner.ui.model.FilterBoxUIState
import com.example.guitartuner.ui.model.TuningSettingsUIState
import com.example.guitartuner.ui.settings.components.TuningControls
import com.example.guitartuner.ui.settings.components.TuningControls.SectionHeader
import com.example.guitartuner.ui.settings.components.TuningControls.TuningSettingsItem
import com.example.guitartuner.ui.theme.PreviewWrapper
import com.rohankhayech.android.util.ui.preview.ThemePreview
import org.koin.androidx.compose.navigation.koinNavViewModel

@Composable
fun SettingsTuningsScreen(navigateToUp: () -> Unit = {}) {
    val vm = koinNavViewModel<SettingsViewModel>()
    val currentTuningState by vm.currentTuningSet.collectAsState()
    val instrumentsFilter by vm.filtersInstrumentState.collectAsState()
    val stringsFilter by vm.filtersStringsState.collectAsState()
    val tunings by vm.listTuningsState.collectAsState()

    SettingsTuningsScreenContent(
        currentTuningState,
        tuningSets = tunings,
        onSelectTuning = {
            vm.selectTuning(it)
            navigateToUp()
        },
        onToggleFavorite = vm::toggleFavoriteTuning,
        onRemoveTuning = vm::deleteTuning,
        instrumentsFilter = instrumentsFilter,
        stringsFilter = stringsFilter,
        onToggleGeneralFilter = vm::toggleFilterGeneral,
        onToggleInstrumentFilter = vm::toggleFilterInstrument,
        onToggleStringsFilter = vm::toggleFilterStrings,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SettingsTuningsScreenContent(
    currentTuningState: TuningSettingsUIState?,
    tuningSets: List<TuningSettingsUIState> = emptyList(),
    onToggleFavorite: (Int, Boolean) -> Unit = { _, _ -> },
    onSelectTuning: (Int) -> Unit = {},
//    onSaveTuning: (TuningSettingsUIState) -> Unit = {},
    onRemoveTuning: (Int) -> Unit = {},
    instrumentsFilter: List<FilterBoxUIState<Int>>?,
    stringsFilter: List<FilterBoxUIState<Int>>?,
    onToggleGeneralFilter: (TuningFilter.General, Boolean) -> Unit,
    onToggleInstrumentFilter: (Int, Boolean) -> Unit,
    onToggleStringsFilter: (Int, Boolean) -> Unit,
) {
    val names = TuningFilter.General.entries.map {
        stringResource(
            when (it) {
                TuningFilter.General.ALL -> R.string.settings_tunings_filter_general_all
                TuningFilter.General.FAVORITES -> R.string.settings_tunings_filter_general_favorites
            }
        )
    }
    val generalFilters by rememberSaveable(key = "general-filters") {
        mutableStateOf(TuningFilter.General.entries.map {
            FilterBoxUIState(
                key = "general_filter_" + it.name,
                value = it,
                text = names[it.ordinal],
                isEnabled = true,
            )
        })
    }

    LazyColumn {
        currentTuningState?.let {
            stickyHeader("current-header") { SectionHeader(title = stringResource(R.string.settings_tunings_current_header)) }
            item("current-${currentTuningState.tuningId}") {
                TuningSettingsItem(tuning = currentTuningState,
                    onSelect = onSelectTuning,
                    onFavSelect = onToggleFavorite,
                    onCustomSave = {})
            }
        }

        stickyHeader("general-header") { SectionHeader(title = stringResource(R.string.settings_tunings_other_header)) }
        item("filter-bar-general") {
            TuningControls.FilterBox(
                values = generalFilters, onSelect = onToggleGeneralFilter
            )
        }

        instrumentsFilter?.let {
            stickyHeader("instrument-header") { SectionHeader(title = stringResource(R.string.settings_tunings_instrument_header)) }
            item("filter-bar-instrument") {
                TuningControls.FilterBox(
                    values = instrumentsFilter, onSelect = onToggleInstrumentFilter
                )
            }
        }

        stringsFilter?.let {
            stickyHeader("instrument-details-header") { SectionHeader(title = stringResource(R.string.settings_tunings_instrument_details_header)) }
            item("filter-bar-instrument-details") {
                TuningControls.FilterBox(
                    values = stringsFilter.map {
                        it.copy(
                            text = it.text + pluralStringResource(
                                R.plurals.settings_tunings_instrument_details_suffix,
                                it.value,
                            )
                        )
                    }, onSelect = onToggleStringsFilter
                )
            }
        }

        item("filter-bar-instrument-details-divider") {
            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }


        items(count = tuningSets.size, key = { it -> tuningSets[it].tuningId }) { index ->
            val tuning = tuningSets[index]
            TuningSettingsItem(tuning = tuning,
                onSelect = onSelectTuning,
                onFavSelect = onToggleFavorite,
                onCustomSave = {})
        }
    }
}

private val previewTuningSettingsUIState by lazy {
    TuningSettingsUIState(
        tuningId = 0,
        instrumentName = "Guitar",
        instrumentDetails = "6 strings",
        tuningName = "Standard",
        notesList = "E, A, D, G, B, E",
        isFavorite = false,
        isCustom = false,
    )
}

private val previewInstrumentsFilter by lazy {
    listOf(
        FilterBoxUIState(
            key = "instrument_filter_0",
            value = 0,
            text = "Guitar",
            isEnabled = true,
        ),
        FilterBoxUIState(
            key = "instrument_filter_1",
            value = 1,
            text = "Bass",
            isEnabled = true,
        ),
        FilterBoxUIState(
            key = "instrument_filter_2",
            value = 2,
            text = "Electric Guitar",
            isEnabled = true,
        ),
    )
}

private val previewStringsFilter by lazy {
    listOf(
        FilterBoxUIState(
            key = "strings_filter_0",
            value = 0,
            text = "6 strings",
            isEnabled = true,
        ),
        FilterBoxUIState(
            key = "strings_filter_1",
            value = 1,
            text = "4 strings",
            isEnabled = true,
        ),
        FilterBoxUIState(
            key = "strings_filter_2",
            value = 2,
            text = "7 strings",
            isEnabled = true,
        ),
    )
}

@Composable
@ThemePreview
private fun SettingsTuningsScreenPreview() {
    PreviewWrapper {
        SettingsTuningsScreenContent(previewTuningSettingsUIState,
            listOf(previewTuningSettingsUIState),
            { _, _ -> },
            { _ -> },
            { _ -> },
            previewInstrumentsFilter,
            previewStringsFilter,
            { _, _ -> },
            { _, _ -> },
            { _, _ -> })
    }
}