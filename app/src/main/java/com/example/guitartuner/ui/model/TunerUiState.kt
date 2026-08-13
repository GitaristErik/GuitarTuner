package com.example.guitartuner.ui.model

import com.example.guitartuner.domain.repository.tuner.PermissionManager

/**
 * Immutable UI state for the tuner screen (unidirectional data flow).
 */
data class TunerUiState(
    val permission: PermissionManager.PermissionState = PermissionManager.PermissionState(
        hasRequiredPermissions = false,
        canRequest = true,
    ),
    val noteOffset: Double? = null,
    val isTuned: Boolean = false,
    val autoDetect: Boolean = true,
    val selectedString: Int? = null,
    val selectedTuningId: Int = 1,
    val buttons: TuneButtonsUIState? = null,
    val tunings: List<TuningUIState> = emptyList(),
    val currentTuning: TuningUIState = TuningUIState(0, "", ""),
    /** Per-string tuned flags; always a new list instance on update. */
    val tunedStrings: List<Boolean> = emptyList(),
    val errorMessage: String? = null,
)

sealed interface TunerEvent {
    data object RequestPermission : TunerEvent
    data class SelectString(val stringId: Int) : TunerEvent
    data class SelectTuning(val tuningId: Int) : TunerEvent
    data object ToggleAutoDetect : TunerEvent
    data class SetAutoDetect(val enabled: Boolean) : TunerEvent
    data class TuneUpString(val stringId: Int) : TunerEvent
    data class TuneDownString(val stringId: Int) : TunerEvent
    data object TuneUpTuning : TunerEvent
    data object TuneDownTuning : TunerEvent
    data object ClearError : TunerEvent
}
