package com.example.guitartuner.ui.model

data class TuningSettingsUIState (
    val tuningId: Int,
    val instrumentName: String,
    val instrumentDetails: String,
    val tuningName: String,
    val notesList: String,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)